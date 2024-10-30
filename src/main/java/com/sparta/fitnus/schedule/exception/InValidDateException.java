package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class InValidDateException extends FitNusException {
    public InValidDateException() {
        super("Invalid date", HttpStatus.BAD_REQUEST);
    }
}
