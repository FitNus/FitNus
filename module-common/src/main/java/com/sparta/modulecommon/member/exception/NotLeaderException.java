package com.sparta.modulecommon.member.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotLeaderException extends FitNusException {
    public NotLeaderException() {
        super("Not leader", HttpStatus.FORBIDDEN);
    }
}
