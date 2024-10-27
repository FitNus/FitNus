package com.sparta.fitnus.schedule.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.service.ScheduleService;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/v1/schedules")
    public ApiResponse<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.createSchedule(authUser, scheduleRequest));
    }

    @PutMapping("/v1/schedules/{id}")
    public ApiResponse<ScheduleResponse> updateSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id,
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.updateSchedule(authUser, id, scheduleRequest));
    }

    @DeleteMapping("/v1/schedules/{id}")
    public ApiResponse<String> deleteSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id
    ) {
        return ApiResponse.createSuccess(scheduleService.deleteSchedule(authUser, id));
    }

    @GetMapping("/v1/schedules")
    public ApiResponse<List<ScheduleResponse>> getMonthlySchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) Integer month
    ) {
        return ApiResponse.createSuccess(scheduleService.getMonthlyScheduleList(authUser, month));
    }
}
