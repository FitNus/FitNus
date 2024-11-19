package com.sparta.service.fitness.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class FitnessgetAllAccessDeniedException extends FitNusException {
    public FitnessgetAllAccessDeniedException() {
        super("해당 센터를 만든 센터장만 접근할 수 있습니다.", HttpStatus.UNAUTHORIZED);
    }
}
