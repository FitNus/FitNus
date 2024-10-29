package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class AlreadyMemberException extends FitNusException {
    public AlreadyMemberException() {
        super("Already member");
    }
}
