package com.sparta.fitnus.timeslot.exception;

import com.sparta.fitnus.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotAvailableTimeslot extends FitNusException {
    public NotAvailableTimeslot() {
        super("Not available timeslot", HttpStatus.FORBIDDEN);
    }
}
