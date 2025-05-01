package com.chemist.system.security.security.controller;


import com.chemist.system.security.security.jwt.JwtUtils;
import com.chemist.system.security.security.userServices.UserDetailsImpl;
import com.chemist.system.security.security.userServices.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class TokenRefreshController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            // Validate the refresh token
            if (jwtUtils.validateJwtToken(requestRefreshToken)) {
                String phoneNo = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);
                
                // Load user details to create new authentication
                UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNo);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // Generate new access token
                String newToken = jwtUtils.generateJwtToken(authentication);
                
                // Optionally generate new refresh token (rotate refresh tokens)
                String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

                // Get additional user details
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
                List<String> roles = userDetailsImpl.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(
                        newToken,
                        newRefreshToken,
                        "Bearer",
                        userDetailsImpl.getId(),
                        userDetailsImpl.getUsername(),
                        userDetailsImpl.getPhoneNo(),
                        userDetailsImpl.getPhoneNo(),
                        userDetailsImpl.getChemist(),
                        roles,
                        userDetailsImpl.getChemistId()

                ));
            }
        } catch (Exception e) {
        }

        return ResponseEntity.badRequest().body("Invalid refresh token");
    }
}

