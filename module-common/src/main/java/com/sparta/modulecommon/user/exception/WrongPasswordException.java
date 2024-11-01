package com.sparta.modulecommon.user.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class WrongPasswordException extends FitNusException {
    public WrongPasswordException() {
        super("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED);
    }
}
