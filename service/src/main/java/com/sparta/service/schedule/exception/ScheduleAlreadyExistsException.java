package com.sparta.service.schedule.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ScheduleAlreadyExistsException extends FitNusException {
    public ScheduleAlreadyExistsException() {
        super("Schedule Already exists", HttpStatus.CONFLICT);
    }
}
