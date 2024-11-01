package com.sparta.modulecommon.center.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class CenterNotFoundException extends FitNusException {
    public CenterNotFoundException() {
        super("해당 센터 ID를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
