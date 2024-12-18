package com.sparta.service.timeslot.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class NotAvailableTimeslot extends FitNusException {
    public NotAvailableTimeslot() {
        super("Not available timeslot", HttpStatus.FORBIDDEN);
    }
}
