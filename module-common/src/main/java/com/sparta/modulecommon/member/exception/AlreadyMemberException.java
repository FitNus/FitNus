package com.sparta.modulecommon.member.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyMemberException extends FitNusException {
    public AlreadyMemberException() {
        super("Already member", HttpStatus.CONFLICT);
    }
}
