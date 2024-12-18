package com.sparta.service.center.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class LocationNotFoundException extends FitNusException {
    public LocationNotFoundException(){
        super("센터 위치를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
