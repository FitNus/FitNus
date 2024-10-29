package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class NotLeaderException extends FitNusException {
    public NotLeaderException() {
        super("해당 모임을 관리할 권한이 없습니다.");
    }
}
