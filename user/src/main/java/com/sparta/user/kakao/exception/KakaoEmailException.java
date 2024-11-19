package com.sparta.user.kakao.exception;

import com.sparta.common.exception.FitNusException;
import org.springframework.http.HttpStatus;

public class KakaoEmailException extends FitNusException {
    public KakaoEmailException() {
        super("카카오 계정에 이메일 정보가 없습니다.", HttpStatus.NOT_FOUND);
    }
}
