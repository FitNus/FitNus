package com.sparta.service.timeslot.controller;

import com.sparta.common.apipayload.ApiResponse;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.timeslot.dto.request.TimeslotDeleteRequest;
import com.sparta.service.timeslot.dto.request.TimeslotRequest;
import com.sparta.service.timeslot.dto.response.TimeslotResponse;
import com.sparta.service.timeslot.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TimeslotController {

    private final TimeslotService timeslotService;

    @PostMapping("/v1/timeslots")
    public ApiResponse<TimeslotResponse> createTimeslot(@AuthenticationPrincipal AuthUser authUser,
                                                        @RequestBody TimeslotRequest request) {
        return ApiResponse.createSuccess(timeslotService.createTimeslot(authUser, request));
    }

    @GetMapping("/v1/timeslots/{id}")
    public ApiResponse<TimeslotResponse> getTimeslot(@PathVariable Long id) {
        return ApiResponse.createSuccess(timeslotService.getTimeslot(id));
    }

    @GetMapping("/v1/timeslots")
    public ApiResponse<List<TimeslotResponse>> getAllTimeslot() {
        return ApiResponse.createSuccess(timeslotService.getAllTimeslot());
    }

    @DeleteMapping("v1/timeslots/{id}")
    public ApiResponse<String> deleteTimeslot(@AuthenticationPrincipal AuthUser authUser,
                                              @PathVariable Long id,
                                              @RequestBody TimeslotDeleteRequest request) {
        timeslotService.deleteTimeslot(authUser, id, request);
        return ApiResponse.createSuccess("타임슬롯이 정상적으로 삭제되었습니다.");
    }
}
