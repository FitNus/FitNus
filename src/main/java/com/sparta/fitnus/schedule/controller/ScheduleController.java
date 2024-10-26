package com.sparta.fitnus.schedule.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.service.ScheduleService;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/v1/schedule")
    public ApiResponse<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ScheduleRequest scheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.createSchedule(authUser, scheduleRequest));
    }
}
