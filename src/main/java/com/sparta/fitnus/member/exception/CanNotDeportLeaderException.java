package com.sparta.fitnus.member.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class CanNotDeportLeaderException extends FitNusException {
    public CanNotDeportLeaderException() {
        super("Can not deport leader");
    }
}
