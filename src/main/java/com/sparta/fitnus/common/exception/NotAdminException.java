package com.sparta.fitnus.common.exception;

public class NotAdminException extends RuntimeException{
    public NotAdminException() {
        super("관리자가 아닙니다.");
    }
}
