package com.sparta.fitnus.user.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class WrongOwnerTokenException extends FitNusException {
    public WrongOwnerTokenException() {
        super("센터 사업자 가입 인증암호가 일치하지 않아 등록이 불가능합니다", HttpStatus.UNAUTHORIZED);
    }
}
