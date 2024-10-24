package com.sparta.fitnus.user.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.dto.request.UserBioRequest;
import com.sparta.fitnus.user.dto.response.UserBioResponse;
import com.sparta.fitnus.user.dto.response.UserGetResponse;
import com.sparta.fitnus.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileBioService;

    @GetMapping("/v1/users/{id}")
    public ApiResponse<UserGetResponse> getUser(@PathVariable Long id) {
        return ApiResponse.createSuccess(profileBioService.getUser(id));
    }

    @PutMapping("/v1/users/{id}/bio")
    public ApiResponse<UserBioResponse> updateBio(@PathVariable Long id,
            @Valid @RequestBody UserBioRequest request) {
        return ApiResponse.createSuccess(profileBioService.updateBio(id, request));
    }

}
