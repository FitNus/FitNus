package com.sparta.fitnus.user.dto.response;

import lombok.Getter;

@Getter
public class AuthTokenResponse {
    private final String accessToken;
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
