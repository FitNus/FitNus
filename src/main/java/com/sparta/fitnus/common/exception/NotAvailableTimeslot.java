package com.sparta.fitnus.common.exception;

public class NotAvailableTimeslot extends RuntimeException {
    public NotAvailableTimeslot() {
        super("Not available timeslot");
    }
}
