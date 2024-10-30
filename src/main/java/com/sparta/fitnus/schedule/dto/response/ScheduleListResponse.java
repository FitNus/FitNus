package com.sparta.fitnus.schedule.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleListResponse {

    private final List<ScheduleResponse> scheduleResponseList;
    private final Integer totalRequiredCoupon;

    public ScheduleListResponse(List<ScheduleResponse> scheduleResponseList, int totalRequiredCoupon) {
        this.scheduleResponseList = scheduleResponseList;
        this.totalRequiredCoupon = totalRequiredCoupon;
    }
}
