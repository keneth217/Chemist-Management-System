package com.chemist.system.security.security.userServices;

import com.chemist.system.config.tenants.TenantContext;
import com.chemist.system.exceptions.ChemistNotActivatedException;
import com.chemist.system.exceptions.TenantNotResolvedException;
import com.chemist.system.models.Chemist;
import com.chemist.system.repository.tenant.ChemistRepository;
import com.chemist.system.repository.central.MasterRepository;
import com.chemist.system.repository.tenant.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final ChemistRepository chemistRepository;
    private final MasterRepository masterRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String tenantId = TenantContext.getCurrentTenant();

        if (tenantId == null || tenantId.isBlank()) {
            throw new TenantNotResolvedException("Tenant context not resolved for authentication");
        }

        try {
            return "master".equalsIgnoreCase(tenantId)
                    ? loadMasterUser(username)
                    : loadTenantUser(username, tenantId);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (ChemistNotActivatedException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    private UserDetails loadMasterUser(String username) {
        return masterRepository.findByPhoneNo(username)
                .map(masterUser -> {
                    if (!masterUser.isActive()) {
                        throw new UsernameNotFoundException("Master user account is inactive: " + username);
                    }
                    return UserDetailsImpl.buildForMasterUser(masterUser);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Master user not found: " + username));
    }

    private UserDetails loadTenantUser(String username, String tenantId) {
        Chemist chemist = chemistRepository.findByChemistId(tenantId)
                .orElseThrow(() -> new UsernameNotFoundException("Chemist tenant not found: " + tenantId));

        if (Boolean.FALSE.equals(chemist.getActivated())) {
            throw new ChemistNotActivatedException("Chemist account not activated: " + tenantId);
        }

        return userRepository.findByPhoneNo(username)
                .map(user -> {
                    if (!user.isActive()) {
                        throw new UsernameNotFoundException("User account is inactive: " + username);
                    }
                    return UserDetailsImpl.build(user, chemist);
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}