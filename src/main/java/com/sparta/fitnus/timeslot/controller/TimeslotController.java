package com.sparta.fitnus.timeslot.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.timeslot.dto.request.TimeslotRequest;
import com.sparta.fitnus.timeslot.dto.response.TimeslotResponse;
import com.sparta.fitnus.timeslot.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TimeslotController {

    private final TimeslotService timeslotService;

    @PostMapping("/v1/timeslot")
    public ApiResponse<TimeslotResponse> createTimeslot(@RequestBody TimeslotRequest request) {
        return ApiResponse.createSuccess(timeslotService.createTimeslot(request));
    }

    @GetMapping("/v1/timeslot/{id}")
    public ApiResponse<TimeslotResponse> getTimeslot(@PathVariable Long id) {
        return ApiResponse.createSuccess(timeslotService.getTimeslot(id));
    }
}
