package com.sparta.fitnus.common.exception;

public class UserBannedException extends RuntimeException {

    public UserBannedException() {
        super("차단된 사용자입니다.");
    }
}
