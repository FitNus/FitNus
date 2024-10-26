package com.sparta.fitnus.timeslot.dto.response;

import com.sparta.fitnus.timeslot.entity.Timeslot;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TimeslotResponse {

    private final Long id;
    private final Long fitnessId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public TimeslotResponse(Timeslot timeslot) {
        id = timeslot.getId();
        fitnessId = timeslot.getFitness().getId();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
    }
}
