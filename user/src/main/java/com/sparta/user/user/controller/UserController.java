package com.sparta.user.user.controller;

import com.sparta.common.apipayload.ApiResponse;
import com.sparta.common.config.CouponService;
import com.sparta.common.config.JwtUtil;
import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.common.user.dto.UserRequest;
import com.sparta.common.user.entity.User;
import com.sparta.user.user.dto.request.ChangePasswordRequest;
import com.sparta.user.user.dto.request.CouponUsageRequest;
import com.sparta.user.user.dto.request.PasswordReset;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import com.sparta.user.user.dto.response.UserResponse;
import com.sparta.user.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final CouponService couponService;

    @PostMapping("/v1/auth/signup")
    public ApiResponse<UserResponse> signup(@RequestBody UserRequest userRequest) {
        return ApiResponse.createSuccess(userService.signup(userRequest));
    }

    @PostMapping("/v1/auth/login")
    public ApiResponse<String> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        AuthTokenResponse authTokenResponse = userService.login(userRequest);
        //쿠키에 accessToken와 refreshToken를 저장
        jwtUtil.setTokenCookie(response, authTokenResponse.getAccessToken());
        jwtUtil.setRefreshTokenCookie(response, authTokenResponse.getRefreshToken());
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

    @PostMapping("/v1/user/use-coupons")
    public ApiResponse<String> useCoupons(@RequestBody CouponUsageRequest request, @AuthenticationPrincipal AuthUser authUser) {
        try {
            couponService.useCoupons(authUser.getId(), request.getQuantity());
            return ApiResponse.createSuccess(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.createError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PostMapping("v1/request/reset-password")
    public ApiResponse<String> resetPassword(@RequestParam String email) {
        return ApiResponse.createSuccess(userService.requestPasswordReset(email));
    }

    @PostMapping("/v1/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody PasswordReset passwordReset) {
        return ApiResponse.createSuccess(userService.resetPassword(passwordReset.getEmail(), passwordReset.getCode(), passwordReset.getNewPassword()));
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
