package com.sparta.fitnus.timeslot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotRequest {

    private Long fitnessId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
