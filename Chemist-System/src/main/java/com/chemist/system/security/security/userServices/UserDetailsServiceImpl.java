package com.chemist.system.security.security.userServices;

import com.chemist.system.config.tenants.TenantContext;
import com.chemist.system.exceptions.ChemistNotActivatedException;
import com.chemist.system.exceptions.TenantNotResolvedException;
import com.chemist.system.master.repository.MasterUserRepository;
import com.chemist.system.tenant.model.Chemist;
import com.chemist.system.tenant.repository.ChemistRepository;
import com.chemist.system.tenant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private static final String MASTER_TENANT = "master";

    private final UserRepository userRepository;
    private final ChemistRepository chemistRepository;
    private final MasterUserRepository masterRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String tenantId = TenantContext.getCurrentTenant();

        // Default to master tenant for excluded endpoints (like login)
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = MASTER_TENANT;
            TenantContext.setCurrentTenant(tenantId);
            logger.debug("Defaulting to master tenant for user: {}", username);
        }

        if (MASTER_TENANT.equalsIgnoreCase(tenantId)) {
            logger.info("Authenticating master user: {}", username);
            return loadMasterUser(username);
        } else {
            try {
                Long chemistId = Long.parseLong(tenantId);
                logger.info("Authenticating tenant user: {} for chemist: {}", username, chemistId);
                return loadTenantUser(username, chemistId);
            } catch (NumberFormatException e) {
                logger.error("Invalid tenant ID format: {}", tenantId);
                throw new TenantNotResolvedException("Invalid tenant ID format");
            }
        }
    }

    private UserDetails loadMasterUser(String username) {
        return masterRepository.findByUsername(username)
                .map(user -> {
                    if (!user.isActive()) {
                        logger.warn("Master user {} is inactive", username);
                        throw new UsernameNotFoundException("Master user account is inactive");
                    }
                    logger.debug("Master user {} authenticated successfully", username);
                    return UserDetailsImpl.buildForMasterUser(user);
                })
                .orElseThrow(() -> {
                    logger.warn("Master user {} not found", username);
                    return new UsernameNotFoundException("Master user not found");
                });
    }

    private UserDetails loadTenantUser(String username, Long chemistId) {
        Chemist chemist = chemistRepository.findById(chemistId)
                .orElseThrow(() -> {
                    logger.warn("Chemist {} not found for user {}", chemistId, username);
                    return new UsernameNotFoundException("Chemist not found");
                });

        if (!chemist.getActivated()) {
            logger.warn("Chemist account {} is not activated", chemistId);
            throw new ChemistNotActivatedException("Chemist account not activated");
        }

        return userRepository.findByUsernameAndChemistId(username, chemistId)
                .map(user -> {
                    if (!user.isActive()) {
                        logger.warn("User {} is inactive for chemist {}", username, chemistId);
                        throw new UsernameNotFoundException("User account is inactive");
                    }
                    logger.debug("Tenant user {} authenticated successfully", username);
                    return UserDetailsImpl.build(user, chemist);
                })
                .orElseThrow(() -> {
                    logger.warn("User {} not found for chemist {}", username, chemistId);
                    return new UsernameNotFoundException("User not found");
                });
    }
}