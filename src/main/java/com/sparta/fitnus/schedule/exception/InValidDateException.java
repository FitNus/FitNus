package com.sparta.fitnus.schedule.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class InValidDateException extends FitNusException {
    public InValidDateException() {
        super("Invalid date");
    }
}
