package com.sparta.service.schedule.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class FullOfPeopleException extends FitNusException {
    public FullOfPeopleException() {
        super("It's full of people", HttpStatus.BAD_REQUEST);
    }
}
