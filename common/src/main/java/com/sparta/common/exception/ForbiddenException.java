package com.sparta.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends FitNusException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
