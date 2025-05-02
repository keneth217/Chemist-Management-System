package com.chemist.system.config.tenants;

import com.chemist.system.exceptions.NoBusinessIdentifierException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class ChemistInterceptor implements HandlerInterceptor {

    private static final List<String> EXCLUDED_ENDPOINTS = Arrays.asList(
            "/api/chemist/test",
            "/api/chemist/create",
            "/api/master/login",
            "/api/auth/**",
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isExcludedEndpoint(requestURI)) {
            return true;
        }

        String tenantId = request.getHeader("X-Tenant");
        if (tenantId == null || tenantId.isBlank()) {
            try {
                throw new NoBusinessIdentifierException("X-Tenant header is required");
            } catch (NoBusinessIdentifierException e) {
                throw new RuntimeException(e);
            }
        }

        TenantContext.setCurrentTenant(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
        DatabaseContext.clear();
    }

    private boolean isExcludedEndpoint(String requestURI) {
        return EXCLUDED_ENDPOINTS.stream()
                .anyMatch(excluded -> requestURI.startsWith(excluded.replace("**", "")));
    }
}