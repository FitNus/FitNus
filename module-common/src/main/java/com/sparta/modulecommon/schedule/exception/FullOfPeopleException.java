package com.sparta.modulecommon.schedule.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class FullOfPeopleException extends FitNusException {
    public FullOfPeopleException() {
        super("It's full of people", HttpStatus.BAD_REQUEST);
    }
}
