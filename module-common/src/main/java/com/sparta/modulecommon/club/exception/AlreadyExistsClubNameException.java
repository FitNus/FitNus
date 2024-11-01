package com.sparta.modulecommon.club.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AlreadyExistsClubNameException extends FitNusException {
    public AlreadyExistsClubNameException() {
        super("Already exists club name", HttpStatus.CONFLICT);
    }
}
