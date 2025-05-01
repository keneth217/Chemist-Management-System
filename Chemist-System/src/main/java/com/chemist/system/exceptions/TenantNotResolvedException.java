package com.chemist.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class TenantNotResolvedException extends RuntimeException {
    public TenantNotResolvedException(String message) {
        super();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    public static class ExpiredJwtException  extends  RuntimeException{
        private static final long serialVersionUID = 1L;

        public ExpiredJwtException(String message) {
            super(message);
        }

        public ExpiredJwtException(String message, Throwable cause) {
            super(message, cause);
        }

        public ExpiredJwtException(Throwable cause) {
            super(cause);
        }
    }
}
