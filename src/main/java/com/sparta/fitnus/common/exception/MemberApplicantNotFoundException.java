package com.sparta.fitnus.common.exception;

public class MemberApplicantNotFoundException extends RuntimeException {
    public MemberApplicantNotFoundException() {
        super("Member applicant not found");
    }
}
