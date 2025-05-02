package com.chemist.system.security.security.jwt;

import com.chemist.system.config.tenants.DatabaseContext;
import com.chemist.system.config.tenants.TenantContext;
import com.chemist.system.exceptions.ChemistNotActivatedException;
import com.chemist.system.security.security.userServices.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TENANT_HEADER = "X-TenantID";

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String tenantId = request.getHeader(TENANT_HEADER);
                if (tenantId != null) {
                    setupTenantContext(tenantId, response);
                    if (response.isCommitted()) {
                        return;
                    }
                }
                authenticateUser(jwt, request);
            }
        } catch (ChemistNotActivatedException e) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (UsernameNotFoundException e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return;
        } catch (Exception e) {
            logger.error("Authentication error", e);
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        } finally {
            clearContexts();
        }

        filterChain.doFilter(request, response);
    }

    private void setupTenantContext(String tenantId, HttpServletResponse response)
            throws IOException, ChemistNotActivatedException {
        if ("master".equalsIgnoreCase(tenantId)) {
            DatabaseContext.setCurrentDatabase("master");
        } else {
            TenantContext.setCurrentTenant(tenantId);
        }
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String username = jwtUtils.getUsernameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTH_HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }

    private void clearContexts() {
        TenantContext.clear();
        DatabaseContext.clear();
    }
}