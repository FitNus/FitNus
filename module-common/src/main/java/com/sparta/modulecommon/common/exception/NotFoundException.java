package com.sparta.modulecommon.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends FitNusException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
