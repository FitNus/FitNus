package com.sparta.user.user.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends FitNusException {
    public DuplicateEmailException() {
        super("이미 사용중인 이메일 입니다.", HttpStatus.CONFLICT);
    }
}
