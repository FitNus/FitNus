package com.sparta.modulecommon.center.controller;

import com.sparta.modulecommon.center.dto.request.CenterSaveRequest;
import com.sparta.modulecommon.center.dto.request.CenterUpdateRequest;
import com.sparta.modulecommon.center.dto.response.CenterResponse;
import com.sparta.modulecommon.center.service.CenterService;
import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.user.entity.AuthUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CenterController {

    private final CenterService centerService;

    @PostMapping("/v1/centers")
    public ApiResponse<CenterResponse> createCenter(@AuthenticationPrincipal AuthUser authUser,
            @RequestBody CenterSaveRequest request) {
        CenterResponse response = centerService.createCenter(authUser, request);

        return ApiResponse.createSuccess(response);
    }

    @PatchMapping("/v1/centers/{id}")
    public ApiResponse<CenterResponse> updateCenter(@AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id,
            @Valid @RequestBody CenterUpdateRequest centerUpdateRequest) {
        return ApiResponse.createSuccess(
                centerService.updateCenter(authUser, id, centerUpdateRequest));
    }

    @DeleteMapping("/v1/centers/{id}")
    public ApiResponse<String> deleteCenter(@AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long id) {
        centerService.deleteCenter(authUser, id);
        return ApiResponse.createSuccess("센터가 정상적으로 삭제되었습니다.");
    }

    @GetMapping("/v1/centers/{id}")
    public ApiResponse<CenterResponse> getCenter(@PathVariable Long id) {
        return ApiResponse.createSuccess(centerService.getCenter(id));
    }

    @GetMapping("v1/centers/nearby")
    public List<CenterResponse> getNearbyCenters(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam Double radius) {
        return centerService.findNearbyCenters(longitude, latitude, radius);
    }
}