package com.chemist.system.master.service;

import com.chemist.system.master.dto.AuthenticationRequest;
import com.chemist.system.master.dto.AuthenticationResponse;
import com.chemist.system.master.repository.MasterUserRepository;
import com.chemist.system.security.security.jwt.JwtUtils;
import com.chemist.system.security.security.userServices.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional("masterTransactionManager")
public class MasterAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(MasterAuthenticationService.class);

    private final MasterUserRepository masterUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Authentication attempt for username: {}", request.getUsername());

        try {
            // Log incoming request details
            logger.debug("Authentication request received - Username: {}, Password Length: {}",
                    request.getUsername(),
                    request.getPassword() != null ? request.getPassword().length() : 0);

            // Attempt authentication
            logger.debug("Creating authentication token...");
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    );

            logger.debug("Authenticating using AuthenticationManager...");
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Extract user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            logger.info("User authenticated successfully - ID: {}, Username: {}",
                    userDetails.getId(),
                    userDetails.getUsername());

            // Generate JWT token
            logger.debug("Generating JWT token...");
            String jwtToken = jwtUtils.generateJwtToken(authentication);
            logger.debug("JWT token generated successfully");

            // Build response
            AuthenticationResponse response = AuthenticationResponse.builder()
                    .token(jwtToken)
                    .userId(String.valueOf(userDetails.getId()))
                    .username(userDetails.getUsername())
                    .role(String.valueOf(userDetails.getUserRole()))

                    .build();

            logger.info("Authentication successful for user: {}", userDetails.getUsername());
            return response;

        } catch (BadCredentialsException e) {
            logger.warn("Invalid credentials for username: {}", request.getUsername());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");

        } catch (UsernameNotFoundException e) {
            logger.warn("User not found: {}", request.getUsername());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found");

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}. Reason: {}",
                    request.getUsername(),
                    e.getMessage(),
                    e);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication failed");

        } catch (Exception e) {
            logger.error("Unexpected authentication error for user: {}. Error: {}",
                    request.getUsername(),
                    e.getMessage(),
                    e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal authentication error");
        } finally {
            logger.debug("Authentication attempt completed for user: {}", request.getUsername());
        }
    }
}