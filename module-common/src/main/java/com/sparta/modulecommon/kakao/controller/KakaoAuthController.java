package com.sparta.modulecommon.kakao.controller;

import com.sparta.modulecommon.config.JwtUtil;
import com.sparta.modulecommon.kakao.service.KakaoAuthService;
import com.sparta.modulecommon.user.dto.response.AuthTokenResponse;
import com.sparta.modulecommon.user.service.RedisUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthService;
    private final JwtUtil jwtUtil;
    private final RedisUserService redisUserService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.client.redirect}")
    private String kakaoClientRedirect;

    @GetMapping("/v1/auth/kakao/signup-login")
    public String kakaoSignupLogin() {
        return "https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + "&redirect_uri=" + kakaoClientRedirect + "&response_type=code";
    }

    @GetMapping("/v1/auth/kakao/callback")
    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        AuthTokenResponse authResponse = kakaoAuthService.handleKakaoAuth(code);
        //쿠키에 토큰 저장
        jwtUtil.setTokenCookie(response, authResponse.getAccessToken());
        jwtUtil.setRefreshTokenCookie(response, authResponse.getRefreshToken());

        response.sendRedirect("/login-success.html");
    }
}