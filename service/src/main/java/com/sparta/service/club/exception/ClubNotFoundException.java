package com.sparta.service.club.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ClubNotFoundException extends FitNusException {

    public ClubNotFoundException() {
        super("Club not found", HttpStatus.NOT_FOUND);
    }
}
