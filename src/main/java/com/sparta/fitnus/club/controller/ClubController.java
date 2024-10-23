package com.sparta.fitnus.club.controller;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/v1/clubs")
    public ApiResponse<ClubResponse> createClub(@RequestBody ClubRequest request) {
        return ApiResponse.createSuccess(clubService.createClub(request));
    }

    @PutMapping("/v1/clubs/{id}")
    public ApiResponse<ClubResponse> updateClub(@RequestBody ClubRequest request, @PathVariable long id) {
        return ApiResponse.createSuccess(clubService.updateClub(request, id));
    }

    @GetMapping("/v1/clubs/{id}")
    public ApiResponse<ClubResponse> getClub(@PathVariable long id) {
        return ApiResponse.createSuccess(clubService.getClub(id));
    }

    @DeleteMapping("/v1/clubs/{id}")
    public ApiResponse<String> deleteClub(@PathVariable long id) {
        clubService.deleteClub(id);
        return ApiResponse.createSuccess("모임이 정상적으로 삭제되었습니다.");
    }
}
