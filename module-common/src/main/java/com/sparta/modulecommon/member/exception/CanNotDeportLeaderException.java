package com.sparta.modulecommon.member.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class CanNotDeportLeaderException extends FitNusException {
    public CanNotDeportLeaderException() {
        super("Can not deport leader", HttpStatus.FORBIDDEN);
    }
}
