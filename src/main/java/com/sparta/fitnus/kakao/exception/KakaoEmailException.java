package com.sparta.fitnus.kakao.exception;

public class KakaoEmailException extends RuntimeException {
    public KakaoEmailException() {
        super("카카오 계정에 이메일 정보가 없습니다.");
    }
}
