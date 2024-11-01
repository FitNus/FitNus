package com.sparta.modulecommon.applicant.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyApplyException extends FitNusException {
    public AlreadyApplyException() {
        super("Already apply member", HttpStatus.CONFLICT);
    }
}
