package com.sparta.fitnus.club.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class ClubNotFoundException extends FitNusException {
    public ClubNotFoundException() {
        super("Club not found");
    }
}
