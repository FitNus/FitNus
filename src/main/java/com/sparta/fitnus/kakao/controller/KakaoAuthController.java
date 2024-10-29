package com.sparta.fitnus.kakao.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.kakao.service.KakaoAuthService;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.RedisUserService;
import com.sparta.fitnus.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoAuthController {
    private final KakaoAuthService kakaoAuthservice;
    private final UserService userService;
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
    public ApiResponse<String> kakaoCallback(@RequestParam("code") String code) {
        //카카오로그인시 callback으로 access토큰 반환
        String accessToken = kakaoAuthservice.getAccessToken(code);
        return ApiResponse.createSuccess(accessToken);
    }

    // 카카오 회원가입 엔드포인트 (Access Token으로 회원가입)
    @PostMapping("/v1/auth/kakao/signup")
    public ApiResponse<String> kakaoSignup(@RequestHeader("Authorization") String accessToken) {
        //카카오 accessToken에서 email추출
        String email = kakaoAuthservice.getEmailFromKakao(accessToken);
        //해당 email로 프로그램 db에 유저 생성 및 저장
        kakaoAuthservice.registerKakaoUser(email);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/auth/kakao/login")
    public ApiResponse<String> kakaoLogin(@RequestHeader("Authorization") String kakaoAccessToken, HttpServletResponse response) {
        //유저 정보 추출
        String email = kakaoAuthservice.getEmailFromKakao(kakaoAccessToken);
        User user = userService.getUserFromEmail(email);
        //access & refresh token 생성
        String accessToken = kakaoAuthservice.createAccessToken(user);
        String refreshToken = kakaoAuthservice.createRefreshToken(user);
        //access & refresh token redis 저장
        redisUserService.saveTokens(String.valueOf(user.getId()), accessToken, refreshToken);

        //accesstoken 응답 쿠키에 설정
        jwtUtil.setTokenCookie(response, accessToken);
        // Refresh Token을 응답 헤더에 설정
        response.addHeader(HttpHeaders.SET_COOKIE, "refreshToken=" + refreshToken + "; HttpOnly; Path=/; Max-Age=86400");
        return ApiResponse.createSuccess(null);
    }
}
