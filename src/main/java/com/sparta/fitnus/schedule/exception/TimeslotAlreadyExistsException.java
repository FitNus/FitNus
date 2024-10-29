package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class TimeslotAlreadyExistsException extends FitNusException {
    public TimeslotAlreadyExistsException() {
        super("Timeslot Already exists");
    }
}
