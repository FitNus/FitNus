package com.sparta.fitnus.club.controller;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Secured(UserRole.Authority.USER)
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/v1/clubs")
    public ApiResponse<ClubResponse> createClub(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ClubRequest request
    ) {
        return ApiResponse.createSuccess(clubService.createClub(authUser, request));
    }

    @PutMapping("/v1/clubs/{id}")
    public ApiResponse<ClubResponse> updateClub(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id,
            @RequestBody ClubRequest request
    ) {
        return ApiResponse.createSuccess(clubService.updateClub(authUser, request, id));
    }

    @GetMapping("/v1/clubs/{id}")
    public ApiResponse<ClubResponse> getClub(@PathVariable long id) {
        return ApiResponse.createSuccess(clubService.getClub(id));
    }

    @DeleteMapping("/v1/clubs/{id}")
    public ApiResponse<String> deleteClub(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id
    ) {
        clubService.deleteClub(authUser, id);
        return ApiResponse.createSuccess(null);
    }
}
