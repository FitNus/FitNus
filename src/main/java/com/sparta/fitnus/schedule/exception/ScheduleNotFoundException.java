package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends FitNusException {
    public ScheduleNotFoundException() {
        super("Schedule not found", HttpStatus.NOT_FOUND);
    }
}
