package com.sparta.fitnus.kakao.exception;

public class KakaoAccountException extends RuntimeException {

    public KakaoAccountException() {
        super("카카오 계정 정보가 없습니다.");
    }
}
