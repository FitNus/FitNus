package com.sparta.user.user.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class WrongPasswordException extends FitNusException {
    public WrongPasswordException() {
        super("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED);
    }
}
