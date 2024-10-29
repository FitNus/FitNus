package com.sparta.fitnus.common.exception;

import org.springframework.http.HttpStatus;

public class SseNotWorkingException extends FitNusException {
    public SseNotWorkingException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
