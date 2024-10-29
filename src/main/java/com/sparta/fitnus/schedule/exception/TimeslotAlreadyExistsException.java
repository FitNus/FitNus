package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class TimeslotAlreadyExistsException extends FitNusException {
    public TimeslotAlreadyExistsException() {
        super("Timeslot Already exists", HttpStatus.CONFLICT);
    }
}
