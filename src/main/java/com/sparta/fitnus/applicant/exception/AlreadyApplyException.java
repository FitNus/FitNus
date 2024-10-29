package com.sparta.fitnus.applicant.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class AlreadyApplyException extends FitNusException {
    public AlreadyApplyException() {
        super("Already apply member");
    }
}
