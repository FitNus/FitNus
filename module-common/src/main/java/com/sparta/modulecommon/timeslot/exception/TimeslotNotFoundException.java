package com.sparta.modulecommon.timeslot.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class TimeslotNotFoundException extends FitNusException {
    public TimeslotNotFoundException() {
        super("Not found timeslot", HttpStatus.NOT_FOUND);
    }
}
