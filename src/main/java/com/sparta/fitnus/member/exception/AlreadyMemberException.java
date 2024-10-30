package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyMemberException extends FitNusException {
    public AlreadyMemberException() {
        super("Already member", HttpStatus.CONFLICT);
    }
}
