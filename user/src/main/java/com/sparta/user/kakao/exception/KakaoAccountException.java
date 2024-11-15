package com.sparta.user.kakao.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class KakaoAccountException extends FitNusException {

    public KakaoAccountException() {
        super("카카오 계정 정보가 없습니다.", HttpStatus.NOT_FOUND);
    }
}
