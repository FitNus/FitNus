package com.sparta.service.applicant.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class MemberApplicantNotFoundException extends FitNusException {
    public MemberApplicantNotFoundException() {
        super("Member applicant not found", HttpStatus.NOT_FOUND);
    }
}
