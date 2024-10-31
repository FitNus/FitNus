package com.sparta.fitnus.user.dto.response;

import lombok.Getter;

@Getter
public class KakaoAuthResponse {
    private final String accessToken;
    private final String refreshToken;

    public KakaoAuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
