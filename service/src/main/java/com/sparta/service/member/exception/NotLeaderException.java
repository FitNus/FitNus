package com.sparta.service.member.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotLeaderException extends FitNusException {
    public NotLeaderException() {
        super("Not leader", HttpStatus.FORBIDDEN);
    }
}
