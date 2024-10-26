package com.sparta.fitnus.common.exception;

public class NotFoundTimeslotException extends RuntimeException {
    public NotFoundTimeslotException() {
        super("Not found timeslot");
    }
}
