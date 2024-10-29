package com.sparta.fitnus.user.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.user.dto.request.ChangePasswordRequest;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/v1/auth/signup")
    public ApiResponse<UserResponse> signup(@RequestBody UserRequest userRequest) {
        return ApiResponse.createSuccess(userService.signup(userRequest));
    }

    @PostMapping("/v1/auth/login")
    public ApiResponse<String> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        //로그인 유저 db 유효 검증
        User user = userService.checkLogin(userRequest);
        String accessToken = userService.createAccessToken(user);
        String refreshToken = userService.createRefreshToken(user);
        //redis에 토큰 저장
        userService.redisSaveTokens(user.getId(), accessToken, refreshToken);
        //쿠키에 accessToken와 refreshToken를 저장
        jwtUtil.setTokenCookie(response, accessToken);
        jwtUtil.setRefreshTokenCookie(response, refreshToken);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/auth/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal AuthUser authUser, HttpServletResponse response, HttpServletRequest request) {
        userService.deleteRedisToken(authUser);
        jwtUtil.clearAllCookies(request, response);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/user/{userId}/change-password")
    public ApiResponse<String> changePassword(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        return ApiResponse.createSuccess(userService.changePassword(authUser, userId, request));
    }

    @DeleteMapping("/v1/user/{userId}/delete")
    public ApiResponse<String> deleteUser(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId, @RequestBody UserRequest userRequest, HttpServletResponse servletResponse, HttpServletRequest servletRequest) {
        //탈퇴유저 검증
        userService.deleteUser(authUser, userId, userRequest);

        //쿠키에 ACCESS_TOKEN, REFRESH_TOKEN 삭제
        jwtUtil.clearAllCookies(servletRequest, servletResponse);
        return ApiResponse.createSuccess(null);
    }

    @Secured(UserRole.Authority.ADMIN)
    @PutMapping("/v1/admin/{userId}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable Long userId, @AuthenticationPrincipal AuthUser authUser) {
        return ApiResponse.createSuccess(userService.deactivateUser(userId, authUser));
    }

    @GetMapping("/v1/user/info")
    public ApiResponse<UserResponse> getUserInfo(@AuthenticationPrincipal AuthUser authUser) {
        User user = userService.getUser(authUser.getId());
        return ApiResponse.createSuccess(new UserResponse(user));
    }


    // 현재 인증된 사용자 정보에 접근하는 예시
    @GetMapping("/test")
    public ApiResponse<?> getProfile(@AuthenticationPrincipal AuthUser authUser) {
        // 인증된 사용자의 정보에 접근 가능
        Long userId = authUser.getId();
        String email = authUser.getEmail();
        String nickname = authUser.getNickname();
        Collection<? extends GrantedAuthority> authorities = authUser.getAuthorities();

        return ApiResponse.createSuccess(Map.of(
                "userId", userId,
                "email", email,
                "authorities", authorities,
                "nickname", nickname
        ));
    }
}
