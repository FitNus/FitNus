package com.sparta.fitnus.timeslot.dto.response;

import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TimeslotResponse {

    private final Long id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Fitness fitness;

    public TimeslotResponse(Timeslot timeslot) {
        id = timeslot.getId();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        fitness = timeslot.getFitness();
    }
}
