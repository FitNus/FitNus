package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class CanNotDeportLeaderException extends FitNusException {
    public CanNotDeportLeaderException() {
        super("Can not deport leader", HttpStatus.FORBIDDEN);
    }
}
