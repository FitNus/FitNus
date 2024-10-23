package com.sparta.common.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("이미 사용중인 이메일 입니다.");
    }
}
