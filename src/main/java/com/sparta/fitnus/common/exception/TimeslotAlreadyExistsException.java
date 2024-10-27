package com.sparta.fitnus.common.exception;

public class TimeslotAlreadyExistsException extends RuntimeException {
    public TimeslotAlreadyExistsException() {
        super("Timeslot Already exists");
    }
}
