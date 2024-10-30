package com.sparta.fitnus.user.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class WrongAdminTokenException extends FitNusException {
    public WrongAdminTokenException() {
        super("관리자 가입 인증암호가 일치하지 않아 등록이 불가능합니다", HttpStatus.UNAUTHORIZED);
    }
}
