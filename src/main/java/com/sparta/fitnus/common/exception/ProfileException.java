package com.sparta.fitnus.common.exception;

import org.springframework.http.HttpStatus;

public class ProfileException extends FitNusException {
    public ProfileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
