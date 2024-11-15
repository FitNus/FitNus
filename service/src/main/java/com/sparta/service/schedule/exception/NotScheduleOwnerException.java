package com.sparta.service.schedule.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotScheduleOwnerException extends FitNusException {
    public NotScheduleOwnerException() {
        super("Not schedule Owner", HttpStatus.FORBIDDEN);
    }
}
