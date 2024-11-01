package com.sparta.modulecommon.schedule.dto.response;

import com.sparta.modulecommon.schedule.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {

    private final Long scheduleId;

    private final String scheduleName;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final Integer requiredCoupon;

    public ScheduleResponse(Schedule schedule) {
        scheduleId = schedule.getId();
        scheduleName = schedule.getScheduleName();
        startTime = schedule.getStartTime();
        endTime = schedule.getEndTime();
        requiredCoupon = schedule.getRequiredCoupon();
    }
}
