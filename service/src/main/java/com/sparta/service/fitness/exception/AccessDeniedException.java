package com.sparta.service.fitness.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends FitNusException {
    public AccessDeniedException() {
        super("해당 센터를 만든 센터장만 접근할 수 있습니다.", HttpStatus.UNAUTHORIZED);
    }
}
