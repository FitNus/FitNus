package com.sparta.fitnus.common.exception;

public class FitnessNotFoundException extends RuntimeException {
    public FitnessNotFoundException() {
        super("Fitness not found");
    }
}
