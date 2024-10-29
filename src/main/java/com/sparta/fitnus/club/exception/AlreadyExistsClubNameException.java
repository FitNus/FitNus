package com.sparta.fitnus.club.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class AlreadyExistsClubNameException extends FitNusException {
    public AlreadyExistsClubNameException() {
        super("Already exists club name");
    }
}
