package com.sparta.modulecommon.schedule.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ScheduleAlreadyExistsException extends FitNusException {
    public ScheduleAlreadyExistsException() {
        super("Schedule Already exists", HttpStatus.CONFLICT);
    }
}
