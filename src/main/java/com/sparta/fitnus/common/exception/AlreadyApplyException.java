package com.sparta.fitnus.common.exception;

public class AlreadyApplyException extends RuntimeException {
    public AlreadyApplyException() {
        super("Already apply member");
    }
}
