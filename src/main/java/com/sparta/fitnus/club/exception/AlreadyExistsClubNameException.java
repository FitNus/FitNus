package com.sparta.fitnus.club.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsClubNameException extends FitNusException {
    public AlreadyExistsClubNameException() {
        super("Already exists club name", HttpStatus.CONFLICT);
    }
}
