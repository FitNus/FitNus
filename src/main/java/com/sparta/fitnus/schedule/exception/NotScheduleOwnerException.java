package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class NotScheduleOwnerException extends FitNusException {
    public NotScheduleOwnerException() {
        super("Not schedule Owner");
    }
}
