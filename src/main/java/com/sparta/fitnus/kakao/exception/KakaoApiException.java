package com.sparta.fitnus.kakao.exception;

public class KakaoApiException extends RuntimeException {

    public KakaoApiException(String message) {
        super("카카오 API 호출 중 오류가 발생했습니다:" + message);
    }
}
