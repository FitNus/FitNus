package com.sparta.fitnus.common.exception;

public class AlreadyExistsClubNameException extends RuntimeException {
    public AlreadyExistsClubNameException() {
        super("Already exists club name");
    }
}
