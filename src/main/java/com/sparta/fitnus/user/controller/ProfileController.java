package com.sparta.fitnus.user.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.dto.request.UserBioRequest;
import com.sparta.fitnus.user.dto.request.UserNicknameRequest;
import com.sparta.fitnus.user.dto.response.UserAttachFileResponse;
import com.sparta.fitnus.user.dto.response.UserBioResponse;
import com.sparta.fitnus.user.dto.response.UserGetResponse;
import com.sparta.fitnus.user.dto.response.UserNicknameResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/users/images")
    public ApiResponse<UserAttachFileResponse> attachFile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return ApiResponse.createSuccess(profileService.attachFile(authUser, file));
    }

    @GetMapping("/users/{id}")
    public ApiResponse<UserGetResponse> getUser(@PathVariable Long id) {
        return ApiResponse.createSuccess(profileService.getUser(id));
    }

    @PutMapping("/users/bio")
    public ApiResponse<UserBioResponse> updateBio(@AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserBioRequest request) {
        return ApiResponse.createSuccess(profileService.updateBio(authUser, request));
    }

    @PatchMapping("/users/nickname")
    public ApiResponse<UserNicknameResponse> updateNickname(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody UserNicknameRequest request) {
        return ApiResponse.createSuccess(profileService.updateNickname(authUser, request));
    }
}
