package com.sparta.service.member.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyMemberException extends FitNusException {
    public AlreadyMemberException() {
        super("Already member", HttpStatus.CONFLICT);
    }
}
