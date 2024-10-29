package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotScheduleOwnerException extends FitNusException {
    public NotScheduleOwnerException() {
        super("Not schedule Owner", HttpStatus.FORBIDDEN);
    }
}
