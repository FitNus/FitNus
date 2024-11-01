package com.sparta.modulecommon.timeslot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotRequest {

    private Long fitnessId;
    private Long centerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
