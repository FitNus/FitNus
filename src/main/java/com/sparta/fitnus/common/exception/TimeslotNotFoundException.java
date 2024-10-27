package com.sparta.fitnus.common.exception;

public class TimeslotNotFoundException extends RuntimeException {
    public TimeslotNotFoundException() {
        super("Not found timeslot");
    }
}
