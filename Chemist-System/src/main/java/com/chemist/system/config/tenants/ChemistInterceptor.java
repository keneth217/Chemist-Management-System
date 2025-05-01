package com.chemist.system.config.tenants;

import com.chemist.system.exceptions.NoBusinessIdentifierException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
@Component
public class ChemistInterceptor implements HandlerInterceptor {

    private static final String[] EXCLUDED_ENDPOINTS = {"/error"};

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (isExcludedEndpoint(requestURI)) {
            return true;
        }
        String tenantId = request.getHeader("X-Tenant");
        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        } else {
            try {
                throw new NoBusinessIdentifierException("No school identifier provided");
            } catch (NoBusinessIdentifierException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private boolean isExcludedEndpoint(String requestURI) {
        for (String excludedEndpoint : EXCLUDED_ENDPOINTS) {
            if (requestURI.matches(excludedEndpoint.replace("**", ".*"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
        DatabaseContext.clear();
    }
}