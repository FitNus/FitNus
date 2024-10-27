package com.sparta.fitnus.user.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.dto.request.ChangePasswordRequest;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/v1/auth/signup")
    public ApiResponse<UserResponse> signup(@RequestBody UserRequest userRequest) {
        return ApiResponse.createSuccess(userService.signup(userRequest));
    }

    @PostMapping("/v1/auth/login")
    public ApiResponse<String> login(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        return ApiResponse.createSuccess(userService.login(userRequest, response));
    }

    @PostMapping("/v1/auth/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal AuthUser authUser, HttpServletResponse response) {
        return ApiResponse.createSuccess(userService.logout(authUser, response));
    }

    @PostMapping("/v1/user/{userId}/change-password")
    public ApiResponse<String> changePassword(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId, @RequestBody ChangePasswordRequest request) {
        return ApiResponse.createSuccess(userService.changePassword(authUser, userId, request));
    }

    @DeleteMapping("/v1/user/{userId}/delete")
    public ApiResponse<String> deleteUser(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId, @RequestBody UserRequest userRequest, HttpServletResponse response) {
        return ApiResponse.createSuccess(userService.deleteUser(authUser, userId, userRequest, response));
    }

    @PutMapping("/v1/admin/{userId}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable Long userId, @AuthenticationPrincipal AuthUser authUser) {
        return ApiResponse.createSuccess(userService.deactivateUser(userId, authUser));
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
