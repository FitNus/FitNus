package com.sparta.modulecommon.schedule.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class InValidDateException extends FitNusException {
    public InValidDateException() {
        super("Invalid date", HttpStatus.BAD_REQUEST);
    }
}
