package com.chemist.system.security.security.jwt;


import com.chemist.system.models.User;
import com.chemist.system.repository.tenant.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    private final UserRepository userRepository;
    
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByEmail(username)
                    .or(() -> userRepository.findByPhoneNo(username))
                    .orElse(null);
        }
        return null;
    }
    
    public String getCurrentUserDisplayName() {
        User user = getCurrentUser();
        if (user != null) {
            if (user.getChemistId() != null) {
                return "Staff ID: " + user.getChemistId();
            }
            return user.getEmail() != null ? user.getEmail() : user.getPhoneNo();
        }
        return "System";
    }
}