package com.sparta.fitnus.timeslot.entity;

import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.timeslot.dto.request.TimeslotRequest;
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

    private Boolean isDeleted = false;

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

    public void updateTimeslotStatus() {
        isDeleted = true;
    }
}
