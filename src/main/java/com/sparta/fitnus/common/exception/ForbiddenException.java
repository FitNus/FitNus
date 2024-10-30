package com.sparta.fitnus.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends FitNusException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
