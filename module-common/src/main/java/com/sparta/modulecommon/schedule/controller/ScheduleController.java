package com.sparta.modulecommon.schedule.controller;

import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.schedule.dto.request.ClubScheduleRequest;
import com.sparta.modulecommon.schedule.dto.request.FitnessScheduleRequest;
import com.sparta.modulecommon.schedule.dto.response.ScheduleListResponse;
import com.sparta.modulecommon.schedule.dto.response.ScheduleResponse;
import com.sparta.modulecommon.schedule.service.ScheduleService;
import com.sparta.modulecommon.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/v1/schedules/fitness")
    public ApiResponse<ScheduleResponse> createFitnessSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody FitnessScheduleRequest fitnessScheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.createFitnessSchedule(authUser, fitnessScheduleRequest));
    }

    @PostMapping("/v1/schedules/clubs")
    public ApiResponse<ScheduleResponse> createClubSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody ClubScheduleRequest clubScheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.createClubSchedule(authUser, clubScheduleRequest));
    }

    @PutMapping("/v1/schedules/{id}/fitness")
    public ApiResponse<ScheduleResponse> updateFitnessSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id,
            @RequestBody FitnessScheduleRequest fitnessScheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.updateFitnessSchedule(authUser, id, fitnessScheduleRequest));
    }

    @PutMapping("/v1/schedules/{id}/clubs")
    public ApiResponse<ScheduleResponse> updateClubSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id,
            @RequestBody ClubScheduleRequest clubScheduleRequest
    ) {
        return ApiResponse.createSuccess(scheduleService.updateClubSchedule(authUser, id, clubScheduleRequest));
    }

    @DeleteMapping("/v1/schedules/{id}")
    public ApiResponse<String> deleteSchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long id
    ) {
        scheduleService.deleteSchedule(authUser, id);
        return ApiResponse.createSuccess(null);
    }

    @GetMapping("/v1/schedules")
    public ApiResponse<ScheduleListResponse> getScheduleList(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day
    ) {
        return ApiResponse.createSuccess(scheduleService.getScheduleList(authUser, year, month, day));
    }

    @PostMapping("/v1/schedules/copy")
    public ApiResponse<String> copySchedule(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam Integer yearToCopy,
            @RequestParam Integer monthToCopy,
            @RequestParam Integer copiedYear,
            @RequestParam Integer copiedMonth
    ) {
        scheduleService.copySchedule(authUser, yearToCopy, monthToCopy, copiedYear, copiedMonth);
        return ApiResponse.createSuccess(null);
    }
}
