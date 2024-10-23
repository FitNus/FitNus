package com.sparta.fitnus.club.controller;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/v1/clubs")
    public ApiResponse<ClubResponse> createClub(@RequestBody ClubRequest request) {
        return ApiResponse.createSuccess(clubService.createClub(request));
    }
}
