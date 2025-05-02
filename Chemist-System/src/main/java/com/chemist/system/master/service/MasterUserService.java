package com.chemist.system.master.service;

import com.chemist.system.master.dto.AuthenticationResponse;
import com.chemist.system.master.dto.RegisterMasterUserRequest;
import com.chemist.system.master.model.MasterUser;
import com.chemist.system.master.repository.MasterUserRepository;
import com.chemist.system.security.security.jwt.JwtUtils;
import com.chemist.system.tenant.dto.ChemistProfileDTO;
import com.chemist.system.tenant.model.ERole;
import com.chemist.system.tenant.service.ChemistService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional("masterTransactionManager")
public class MasterUserService {

    private final MasterUserRepository masterUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChemistService chemistService;
    private final JwtUtils jwtUtils;

    public MasterUserService(MasterUserRepository masterUserRepository,
                             PasswordEncoder passwordEncoder,
                             ChemistService chemistService,
                             JwtUtils jwtUtils) {
        this.masterUserRepository = masterUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.chemistService = chemistService;
        this.jwtUtils = jwtUtils;
    }

    @PostConstruct
    public void initMasterUser() {
        if (masterUserRepository.count() == 0) {
            createDefaultMasterUser();
        }
    }

    private void createDefaultMasterUser() {
        String defaultUsername = "admin";
        String defaultPassword = "admin";
        String defaultEmail = "admin@chemist.com";
        String defaultPhone = "0711766223";

        if (masterUserRepository.findByUsername(defaultUsername).isEmpty()) {
            MasterUser masterUser = MasterUser.builder()
                    .email(defaultEmail)
                    .firstName("System")
                    .lastName("Admin")
                    .password(passwordEncoder.encode(defaultPassword))
                    .role(ERole.ADMIN.name())
                    .username(defaultUsername)
                    .phoneNo(defaultPhone)
                    .active(true)
                    .build();

            masterUserRepository.save(masterUser);
            System.out.println("Default admin user created with username: " + defaultUsername);
        }
    }

    public Optional<MasterUser> findByPhoneNo(String phoneNo) {
        return masterUserRepository.findByPhoneNo(phoneNo);
    }

    public void registerNewChemist(String masterUsername, ChemistProfileDTO profileDTO) {
        MasterUser masterUser = masterUserRepository.findByUsername(masterUsername)
                .orElseThrow(() -> new RuntimeException("Master user not found"));

        if (!ERole.ADMIN.name().equals(masterUser.getRole())) {
            throw new RuntimeException("Only admin users can register new chemists");
        }

        chemistService.saveProfile(profileDTO);
    }

    public AuthenticationResponse registerAdmin(RegisterMasterUserRequest request) {
        if (masterUserRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (masterUserRepository.existsByPhoneNo(request.getPhoneNo())) {
            throw new RuntimeException("Phone number already registered");
        }

        if (!isPasswordValid(request.getPassword())) {
            throw new RuntimeException("Password must be at least 8 characters with letters, numbers and special chars");
        }

        MasterUser user = MasterUser.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ERole.ADMIN.name())
                .email(request.getEmail().trim())
                .phoneNo(request.getPhoneNo().trim())
                .active(true)
                .build();

        MasterUser savedUser = masterUserRepository.save(user);

        String jwtToken = jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(
                        savedUser.getUsername(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(savedUser.getRole()))
                )
        );

        return new AuthenticationResponse(
                jwtToken,
                savedUser.getId().toString(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    private boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
        return password != null && password.matches(pattern);
    }

    public MasterUser findByPhoneNoOrThrow(String phoneNo) {
        return masterUserRepository.findByPhoneNo(phoneNo)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}