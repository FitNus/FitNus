package com.sparta.service.applicant.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyApplyException extends FitNusException {
    public AlreadyApplyException() {
        super("Already apply member", HttpStatus.CONFLICT);
    }
}
