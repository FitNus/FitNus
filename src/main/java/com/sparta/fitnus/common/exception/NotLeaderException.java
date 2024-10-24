package com.sparta.fitnus.common.exception;

public class NotLeaderException extends RuntimeException {
    public NotLeaderException() {
        super("해당 모임의 가입 신청을 관리할 권한이 없습니다.");
    }
}
