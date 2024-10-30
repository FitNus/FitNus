package com.sparta.fitnus.user.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.dto.request.ProfileUpdateRequest;
import com.sparta.fitnus.user.dto.response.ProfileAttachFileResponse;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import com.sparta.fitnus.user.dto.response.ProfileUpdateResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<ProfileResponse> getUser(@PathVariable Long id) {
        return ApiResponse.createSuccess(profileService.getUser(id));
    }

    @PutMapping("/users/profile")
    public ApiResponse<ProfileUpdateResponse> updateProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.createSuccess(profileService.updateProfile(authUser, request));
    }
}
