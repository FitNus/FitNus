package com.sparta.fitnus.fitness.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.service.FitnessService;
import com.sparta.fitnus.user.entity.AuthUser;
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

    // 센터등록
    @PostMapping("/v1/fitness")
    public ApiResponse<FitnessResponse> addCenter(@RequestBody FitnessRequest request) {
        return ApiResponse.createSuccess(fitnessService.addFitness(request, request.getCenterId()));
    }

    // 단건조회
    @GetMapping("/v1/fitness/{id}")
    public ApiResponse<FitnessResponse> getFitness(@PathVariable Long id) {
        return ApiResponse.createSuccess(fitnessService.getFitness(id));
    }

    // 다건조회
    @GetMapping("/v1/fitness")
    public ApiResponse<List<FitnessResponse>> getAllFitness() {
        return ApiResponse.createSuccess(fitnessService.getAllFitness());
    }

    // 업데이트
    @PatchMapping("/v1/fitness/{id}")
    public ApiResponse<FitnessResponse> UpdateFitness(@AuthenticationPrincipal AuthUser authUser,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody FitnessRequest fitnessRequest) {
        return ApiResponse.createSuccess(fitnessService.updateFitness(authUser, id, fitnessRequest));
    }

    @DeleteMapping("/v1/fitness/{id}")
    public ApiResponse<String> deleteFitness(@AuthenticationPrincipal AuthUser authUser,
                                             @PathVariable Long id) {
        fitnessService.deleteFitness(authUser, id);
        return ApiResponse.createSuccess("운동종목이 정상적으로 삭제되었습니다.");
    }
}
