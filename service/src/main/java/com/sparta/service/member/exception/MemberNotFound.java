package com.sparta.service.member.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class MemberNotFound extends FitNusException {
    public MemberNotFound() {
        super("Member not found", HttpStatus.NOT_FOUND);
    }
}
