package com.sparta.modulecommon.fitness.controller;

import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.fitness.dto.request.FitnessDeleteRequest;
import com.sparta.modulecommon.fitness.dto.request.FitnessGetAllRequest;
import com.sparta.modulecommon.fitness.dto.request.FitnessRequest;
import com.sparta.modulecommon.fitness.dto.response.FitnessResponse;
import com.sparta.modulecommon.fitness.service.FitnessService;
import com.sparta.modulecommon.user.entity.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FitnessController {
    private final FitnessService fitnessService;

    // 피트니스 등록
    @PostMapping("/v1/fitness")
    public ApiResponse<FitnessResponse> createFitness(@AuthenticationPrincipal AuthUser authUser,
                                                      @RequestBody FitnessRequest request) {
        return ApiResponse.createSuccess(fitnessService.createFitness(authUser, request));
    }

    // 단건조회
    @GetMapping("/v1/fitness/{id}")
    public ApiResponse<FitnessResponse> getFitness(@PathVariable Long id) {
        return ApiResponse.createSuccess(fitnessService.getFitness(id));
    }

    // 다건조회
    @GetMapping("/v1/fitness")
    public ApiResponse<List<FitnessResponse>> getAllFitness(@AuthenticationPrincipal AuthUser authuser,
                                                            @RequestBody FitnessGetAllRequest request) {
        return ApiResponse.createSuccess(fitnessService.getAllFitness(authuser, request.getCenterId()));
    }

    // 업데이트
    @PatchMapping("/v1/fitness/{id}")
    public ApiResponse<FitnessResponse> updateFitness(@AuthenticationPrincipal AuthUser authUser,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody FitnessRequest fitnessRequest) {
        return ApiResponse.createSuccess(fitnessService.updateFitness(authUser, id, fitnessRequest));
    }

    @DeleteMapping("/v1/fitness/{id}")
    public ApiResponse<String> deleteFitness(@AuthenticationPrincipal AuthUser authUser,
                                             @PathVariable Long id,
                                             @RequestBody FitnessDeleteRequest request) {
        fitnessService.deleteFitness(authUser, id, request);
        return ApiResponse.createSuccess("운동종목이 정상적으로 삭제되었습니다.");
    }
}
