package com.sparta.modulecommon.timeslot.entity;

import com.sparta.modulecommon.fitness.entity.Fitness;
import com.sparta.modulecommon.timeslot.dto.request.TimeslotRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_id", nullable = false)
    private Fitness fitness;

    private Timeslot(TimeslotRequest request, Fitness fitness) {
        startTime = request.getStartTime();
        endTime = request.getEndTime();
        this.fitness = fitness;
    }

    public static Timeslot of(TimeslotRequest request, Fitness fitness) {
        return new Timeslot(request, fitness);
    }

}
