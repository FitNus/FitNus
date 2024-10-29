package com.sparta.fitnus.timeslot.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class TimeslotNotFoundException extends FitNusException {
    public TimeslotNotFoundException() {
        super("Not found timeslot", HttpStatus.NOT_FOUND);
    }
}
