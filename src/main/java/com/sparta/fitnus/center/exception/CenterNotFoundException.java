package com.sparta.fitnus.center.exception;

import com.sparta.fitnus.common.exception.FitNusException;

public class CenterNotFoundException extends FitNusException {
    public CenterNotFoundException() {
        super("해당 센터 ID를 찾을 수 없습니다.");
    }
}
