package com.sparta.fitnus.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateNotificationException extends FitNusException{
    public DuplicateNotificationException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
