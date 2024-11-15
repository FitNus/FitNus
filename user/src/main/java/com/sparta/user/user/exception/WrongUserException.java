package com.sparta.user.user.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class WrongUserException extends FitNusException {

    public WrongUserException() {
        super("유저 정보가 일치 하지 않습니다.", HttpStatus.FORBIDDEN);
    }
}
