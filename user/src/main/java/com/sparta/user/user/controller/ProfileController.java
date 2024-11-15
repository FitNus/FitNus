package com.sparta.user.user.controller;

import com.sparta.common.apipayload.ApiResponse;
import com.sparta.common.dto.AuthUser;
import com.sparta.user.user.dto.request.ProfileUpdateRequest;
import com.sparta.user.user.dto.response.ProfileAttachFileResponse;
import com.sparta.user.user.dto.response.ProfileResponse;
import com.sparta.user.user.dto.response.ProfileUpdateResponse;
import com.sparta.user.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/users/images")
    public ApiResponse<ProfileAttachFileResponse> attachFile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        return ApiResponse.createSuccess(profileService.attachFile(authUser, file));
    }

    @DeleteMapping("/users/images")
    public ApiResponse<String> deleteFile(@AuthenticationPrincipal AuthUser authUser) {
        profileService.deleteFile(authUser);
        return ApiResponse.createSuccess(null);
    }

    @GetMapping("/users/{id}")
    public ApiResponse<ProfileResponse> getUserProfile(@PathVariable Long id) {
        return ApiResponse.createSuccess(profileService.getUserProfile(id));
    }

    @PutMapping("/users/profile")
    public ApiResponse<ProfileUpdateResponse> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.createSuccess(profileService.updateProfile(authUser, request));
    }
}
