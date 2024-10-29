package com.sparta.fitnus.club.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ClubNotFoundException extends FitNusException {

    public ClubNotFoundException() {
        super("Club not found", HttpStatus.NOT_FOUND);
    }
}
