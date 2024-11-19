package com.sparta.user.user.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class UserBannedException extends FitNusException {

    public UserBannedException() {
        super("차단된 사용자입니다.", HttpStatus.FORBIDDEN);
    }
}
