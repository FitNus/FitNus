package com.sparta.fitnus.applicant.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class MemberApplicantNotFoundException extends FitNusException {
    public MemberApplicantNotFoundException() {
        super("Member applicant not found");
    }
}
