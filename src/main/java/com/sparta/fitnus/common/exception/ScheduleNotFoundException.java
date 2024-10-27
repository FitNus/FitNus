package com.sparta.fitnus.common.exception;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException() {
        super("Schedule not found");
    }
}
