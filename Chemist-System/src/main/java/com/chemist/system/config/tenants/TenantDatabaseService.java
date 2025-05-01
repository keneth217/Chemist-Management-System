package com.chemist.system.config.tenants;

import com.chemist.system.dto.ChemistProfileDTO;
import com.chemist.system.models.*;

import com.chemist.system.repository.tenant.RoleRepository;
import com.chemist.system.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class TenantDatabaseService {

    private final JdbcTemplate centralJdbcTemplate;
    private final TenantDataSourceProvider provider;
    private final ChemistRegistrationStatusService chemistRegistrationStatusService;
    private final UserService userService;
    private final ChemistService chemistService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final DefaultSettingsService defaultSettingsService;
    private final ChemistRegistrationStatusService statusService;

    @Value("${app.login.url}")
    private String loginUrl;

    @Autowired
    public TenantDatabaseService(JdbcTemplate centralJdbcTemplate,
                                 TenantDataSourceProvider provider,
                                 ChemistRegistrationStatusService chemistRegistrationStatusService, UserService userService,
                                 ChemistService chemistService,
                                 RoleRepository roleRepository,
                                 PasswordEncoder encoder,
                                 EmailService emailService,
                                 DefaultSettingsService defaultSettingsService,
                                 ChemistRegistrationStatusService statusService) {
        this.centralJdbcTemplate = centralJdbcTemplate;
        this.provider = provider;
        this.chemistRegistrationStatusService = chemistRegistrationStatusService;
        this.userService = userService;
        this.chemistService = chemistService;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.emailService = emailService;
        this.defaultSettingsService = defaultSettingsService;
        this.statusService = statusService;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<String> createDatabase(Chemist chemist) {
        String tenantId = String.valueOf(chemist.getChemistId());
        try {
            statusService.updateStatus(tenantId, "Creating database...");
            createTenantDatabase(chemist);
            statusService.updateStatus(tenantId, "Setting up tenant data source...");
            provider.createDataSourceForTenant(chemist);
            TenantContext.setCurrentTenant(tenantId);
            try {
                statusService.updateStatus(tenantId, "Creating admin user...");
                User admin = createAdminUser(chemist);
                statusService.updateStatus(tenantId, "Creating chemist profile...");
                createChemistProfile(chemist, admin);
                statusService.updateStatus(tenantId, "Applying default settings...");
                applyDefaultSettings(chemist);
                statusService.updateStatus(tenantId, "Sending email notification...");
                sendEmailNotification(admin, chemist);
                statusService.updateStatus(tenantId, "Completed");
                return CompletableFuture.completedFuture("Database creation completed for chemist " + tenantId);
            } finally {
                TenantContext.clear();
            }
        } catch (Exception e) {
            statusService.updateStatus(tenantId, "Error: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private void createTenantDatabase(Chemist chemist) {
        String dbName = "chemist_" + chemist.getChemistId();
        centralJdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
    }

    private User createAdminUser(Chemist chemist) {
        User admin = new User();
        admin.setUsername(chemist.getEmail());
        admin.setEmail(chemist.getEmail());
        admin.setPhoneNo(chemist.getPhone());
        admin.setPassword(encoder.encode(chemist.getChemistCode()));
        admin.setChemistId(String.valueOf(chemist.getChemistId()));
        admin.setChemistName(chemist.getChemistName());
        Set<UserRole> roles = new HashSet<>();
        UserRole adminRole = roleRepository.findByName(String.valueOf(ERole.ADMIN))
                .orElseGet(() -> {
                    UserRole newRole = new UserRole();
                    return roleRepository.save(newRole);
                });
        roles.add(adminRole);
        admin.setRoles(roles);
        Set<String> userTypes = new HashSet<>();
        userTypes.add(ERole.ADMIN.name());
        admin.setUserTypes(userTypes);
        return userService.saveUser(admin);
    }

    private void createChemistProfile(Chemist chemist, User admin) {
        ChemistProfileDTO profileDTO = new ChemistProfileDTO();
        profileDTO.setChemistOwnerName(chemist.getChemistOwnerName());
        profileDTO.setEmail(chemist.getEmail());
        profileDTO.setPhone(chemist.getPhone());
        profileDTO.setChemistId(chemist.getChemistId());
        chemistService.saveProfile(profileDTO);
    }

    private void applyDefaultSettings(Chemist chemist) {
        defaultSettingsService.applyDefaultSettings(
                String.valueOf(chemist.getChemistId()),
                chemist.getChemistName(),
                chemist.getEmail(),
                chemist.getChemistCode(),
                chemist.getPhone(),
                chemist.getAddress()
        );
    }

    private void sendEmailNotification(User admin, Chemist chemist) {
        String subject = "Your Chemist Account Setup";
        String content = String.format(
                "Dear %s,%n%nYour chemist account has been successfully created.%n%n" +
                        "Chemist Name: %s%nChemist ID: %s%nLogin URL: %s%n%n" +
                        "Your temporary password is your Chemist Code.%n%n" +
                        "Please change your password after first login.%n%n" +
                        "Thank you,%nThe Chemist System Team",
                admin.getUsername(),
                chemist.getChemistName(),
                chemist.getChemistId(),
                loginUrl
        );
        emailService.sendSimpleMessage(admin.getEmail(), subject, content);
    }
}