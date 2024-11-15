package com.sparta.service.timeslot.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class TimeslotNotFoundException extends FitNusException {
    public TimeslotNotFoundException() {
        super("Not found timeslot", HttpStatus.NOT_FOUND);
    }
}
