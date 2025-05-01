package com.chemist.system.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ChemistNotActivatedException extends RuntimeException {
    public ChemistNotActivatedException(String message) {
        super(message);
    }

    public ChemistNotActivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}