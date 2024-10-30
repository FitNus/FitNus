package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotLeaderException extends FitNusException {
    public NotLeaderException() {
        super("Not leader", HttpStatus.FORBIDDEN);
    }
}
