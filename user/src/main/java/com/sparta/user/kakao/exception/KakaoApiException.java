package com.sparta.user.kakao.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class KakaoApiException extends FitNusException {

    public KakaoApiException(String message) {
        super("카카오 API 호출 중 오류가 발생했습니다:" + message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
