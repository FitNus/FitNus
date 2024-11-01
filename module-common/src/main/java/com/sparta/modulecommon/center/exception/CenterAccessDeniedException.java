package com.sparta.modulecommon.center.exception;

import com.sparta.modulecommon.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class CenterAccessDeniedException extends FitNusException {
    public CenterAccessDeniedException() {
        super("해당 센터를 만든 센터장만 접근할 수 있습니다.", HttpStatus.FORBIDDEN);
    }
}
