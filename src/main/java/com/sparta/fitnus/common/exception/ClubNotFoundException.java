package com.sparta.fitnus.common.exception;

public class ClubNotFoundException extends RuntimeException {
    public ClubNotFoundException() {
        super("Club not found");
    }
}
