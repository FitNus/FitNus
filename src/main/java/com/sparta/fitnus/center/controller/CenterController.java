package com.sparta.fitnus.center.controller;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
import com.sparta.fitnus.center.dto.response.CenterResponse;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CenterController {
    private final CenterService centerService;

    @PostMapping("/v1/centers")
    public ApiResponse<CenterResponse> addCenter(@RequestBody CenterSaveRequest request) {
        CenterResponse response = centerService.addCenter(request);

        return ApiResponse.createSuccess(response);
    }

    @PatchMapping("/v1/{boardId}")
    public ApiResponse<CenterResponse> updateBoard(@AuthenticationPrincipal AuthUser authUser,
                                                   @PathVariable Long boardId,
                                                   @Valid @RequestBody CenterUpdateRequest boardUpdateRequest) {
        return ApiResponse.createSuccess(centerService.updateBoard(authUser, boardId, boardUpdateRequest));
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


}
