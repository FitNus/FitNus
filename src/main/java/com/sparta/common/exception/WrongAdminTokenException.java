package com.sparta.common.exception;

public class WrongAdminTokenException extends RuntimeException {
    public WrongAdminTokenException() {
        super("관리자 가입 인증암호가 일치하지 않아 등록이 불가능합니다");
    }
}
