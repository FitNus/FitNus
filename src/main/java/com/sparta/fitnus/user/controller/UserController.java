package com.sparta.fitnus.user.controller;

import com.sparta.common.ApiResponse;
import com.sparta.fitnus.user.request.UserRequest;
import com.sparta.fitnus.user.response.UserResponse;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/v1/auth/signup")
    public ApiResponse<UserResponse> signup(@RequestBody UserRequest userRequest) {
        ApiResponse<UserResponse> response = userService.signup(userRequest);
        return response;
    }
}
