package com.sparta.modulecommon.schedule.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends FitNusException {
    public ScheduleNotFoundException() {
        super("Schedule not found", HttpStatus.NOT_FOUND);
    }
}
