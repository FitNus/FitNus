package com.sparta.fitnus.common.exception;

public class WrongUserException extends RuntimeException {

    public WrongUserException() {
        super("유저 정보가 일치 하지 않습니다.");
    }
}
