package com.sparta.fitnus.fitness.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.service.FitnessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FitnessController {
    private final FitnessService fitnessService;

    @PostMapping("/v1/fitness/{id}")
    public ApiResponse<FitnessResponse> addCenter(@RequestBody FitnessRequest request, @PathVariable Long id) {
        FitnessResponse response = fitnessService.addFitness(request, id);

        return ApiResponse.createSuccess(response);
    }
}
