package com.sparta.common.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message){
        super(message);
    }
}
