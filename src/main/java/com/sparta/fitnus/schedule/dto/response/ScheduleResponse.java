package com.sparta.fitnus.schedule.dto.response;

import com.sparta.fitnus.schedule.entity.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {

    private final Long scheduleId;

    private final String fitnessName;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final Integer requiredCoupon;

    public ScheduleResponse(Schedule schedule) {
        scheduleId = schedule.getId();
        fitnessName = schedule.getFitnessName();
        startTime = schedule.getStartTime();
        endTime = schedule.getEndTime();
        requiredCoupon = schedule.getRequiredCoupon();
    }
}
