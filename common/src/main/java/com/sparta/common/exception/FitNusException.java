package com.sparta.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FitNusException extends RuntimeException {

    private final HttpStatus httpStatus;

    public FitNusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
