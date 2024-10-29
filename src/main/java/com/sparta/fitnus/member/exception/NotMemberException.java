package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class NotMemberException extends FitNusException {
    public NotMemberException() {
        super("Not member");
    }
}
