package com.chemist.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoBusinessIdentifierException extends RuntimeException {
    public NoBusinessIdentifierException(String message) {
        super(message);
    }
}