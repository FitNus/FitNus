package com.sparta.service.schedule.dto.response;

import com.sparta.service.schedule.entity.Schedule;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
