package com.sparta.fitnus.common.exception;

public class WrongOwnerTokenException extends RuntimeException {
    public WrongOwnerTokenException() {
        super("센터 사업자 가입 인증암호가 일치하지 않아 등록이 불가능합니다");
    }
}
