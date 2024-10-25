package com.sparta.fitnus.fitness.controller;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.response.CenterResponse;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FitnessController {

    @PostMapping("/v1/centers")
    public ApiResponse<CenterResponse> addCenter(@RequestBody CenterSaveRequest request) {
        CenterResponse response = FitnessService.addCenter(request);

        return ApiResponse.createSuccess(response);
    }
}
