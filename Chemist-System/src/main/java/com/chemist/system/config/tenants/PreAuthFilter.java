package com.chemist.system.config.tenants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Component
public class PreAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(PreAuthFilter.class);
    private static final String TENANT_HEADER = "X-Tenant";
    private static final String MASTER_TENANT = "master";

    private static final List<String> EXCLUDED_ENDPOINTS = Arrays.asList(
            "/api/master/login",
            "/api/auth/login",
            "/api/chemist/test",
            "/api/chemist/create",
            "/error",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (shouldSkipTenantCheck(requestURI)) {
            logger.debug("Skipping tenant check for: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            logger.warn("Missing X-Tenant header for request: {}", requestURI);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-Tenant header");
            return;
        }

        try {
            logger.debug("Setting tenant context: {}", tenantId);
            TenantContext.setCurrentTenant(tenantId);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            logger.debug("Cleared tenant context");
        }
    }

    private boolean shouldSkipTenantCheck(String requestURI) {
        return EXCLUDED_ENDPOINTS.stream()
                .anyMatch(endpoint ->
                        requestURI.equals(endpoint) ||
                                requestURI.startsWith(endpoint + "/") ||
                                (endpoint.contains("**") && requestURI.startsWith(endpoint.replace("**", "")))
                );
    }
}