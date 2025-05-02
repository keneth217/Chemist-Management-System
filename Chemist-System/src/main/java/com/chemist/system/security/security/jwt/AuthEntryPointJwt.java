package com.chemist.system.security.security.jwt;


import com.chemist.system.exceptions.ChemistNotActivatedException;
import com.chemist.system.exceptions.TenantNotResolvedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        Throwable cause = authException.getCause();

        if (cause instanceof ChemistNotActivatedException) {
            logger.error("Access denied: {} - {}", cause.getMessage(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error: Chemist is deactivated. Contact admin.");
        } else if (cause instanceof TenantNotResolvedException.ExpiredJwtException) {
            logger.error("JWT token is expired: {} - {}", authException.getMessage(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Token expired. Please refresh your token.");
        } else {
            logger.error("Unauthorized error: {} - {}", authException.getMessage(), request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        }
    }
}