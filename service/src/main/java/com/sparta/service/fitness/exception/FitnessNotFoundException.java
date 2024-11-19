package com.sparta.service.fitness.exception;

public class FitnessNotFoundException extends RuntimeException {
    public FitnessNotFoundException() {
        super("해당 피트니스 Id를 찾을 수 없습니다.");
    }
}
