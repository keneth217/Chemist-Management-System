package com.chemist.system.security.security.jwt;

import com.chemist.system.config.tenants.DatabaseContext;
import com.chemist.system.config.tenants.TenantContext;
import com.chemist.system.exceptions.ChemistNotActivatedException;
import com.chemist.system.models.Chemist;
import com.chemist.system.security.security.userServices.UserDetailsServiceImpl;
import com.chemist.system.services.ChemistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final ChemistService chemistService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils,
                           @Lazy UserDetailsServiceImpl userDetailsService,
                           @Lazy ChemistService chemistService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.chemistService = chemistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String tenantId = request.getHeader("X-TenantID");
            if (tenantId != null) {
                handleTenantContext(tenantId, request, response);
                if (response.isCommitted()) {
                    return; // Response already handled
                }
            }

            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                authenticateUser(jwt, request);
            }
        } catch (Exception e) {
            handleAuthenticationError(e, response);
            return;
        } finally {
            TenantContext.clear();
            DatabaseContext.clear();
        }

        filterChain.doFilter(request, response);
    }

    private void handleTenantContext(String tenantId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if ("master".equalsIgnoreCase(tenantId)) {
            DatabaseContext.setCurrentDatabase("master");
        } else {
            TenantContext.setCurrentTenant(tenantId);
            Chemist chemist = chemistService.findByChemistId(tenantId);
            if (chemist == null || Boolean.FALSE.equals(chemist.getActivated())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chemist account is deactivated.");
            }
        }
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String phoneNo = jwtUtils.getUserNameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNo);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleAuthenticationError(Exception e, HttpServletResponse response) throws IOException {
        logger.error("Cannot set user authentication: {}", e.getMessage());
        if (e instanceof ChemistNotActivatedException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}