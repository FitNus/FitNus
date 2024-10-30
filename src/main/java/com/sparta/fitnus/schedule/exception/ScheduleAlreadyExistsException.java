package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ScheduleAlreadyExistsException extends FitNusException {
    public ScheduleAlreadyExistsException() {
        super("Schedule Already exists", HttpStatus.CONFLICT);
    }
}
