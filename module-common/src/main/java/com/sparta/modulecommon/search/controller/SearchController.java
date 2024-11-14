package com.sparta.modulecommon.search.controller;

import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.search.dto.response.SearchCenterResponse;
import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import com.sparta.modulecommon.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search/clubs")
    public ApiResponse<Page<SearchClubResponse>> searchClubs(
            @RequestParam(required = false) String clubName,
            @RequestParam(required = false) String clubInfo,
            @RequestParam(required = false) String place,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.createSuccess(
                searchService.searchClubs(clubName, clubInfo, place, page, size));
    }

    @GetMapping("/search/centers")
    public ApiResponse<Page<SearchCenterResponse>> searchCentersNearByLocation(
            @RequestParam(required = false) String centerName,
            @RequestParam(required = false) String fitnessName,
            @RequestParam double userLat,
            @RequestParam double userLon,
            @RequestParam double radius,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.createSuccess(
                searchService.searchCentersNearByLocation(centerName, fitnessName, userLat, userLon,
                        radius, page, size));
    }
}
