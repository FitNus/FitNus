package com.sparta.fitnus.applicant.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyApplyException extends FitNusException {
    public AlreadyApplyException() {
        super("Already apply member", HttpStatus.CONFLICT);
    }
}
