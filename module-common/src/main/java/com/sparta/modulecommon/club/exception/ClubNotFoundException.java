package com.sparta.modulecommon.club.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ClubNotFoundException extends FitNusException {

    public ClubNotFoundException() {
        super("Club not found", HttpStatus.NOT_FOUND);
    }
}
