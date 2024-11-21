package com.sparta.service.timeslot.entity;

import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.timeslot.dto.request.TimeslotRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "timeslot")
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_id", nullable = false)
    private Fitness fitness;

    private Timeslot(TimeslotRequest request, Fitness fitness) {
        startTime = request.getStartTime();
        endTime = request.getEndTime();
        this.fitness = fitness;
        capacity = request.getMaxPeople();
    }

    public static Timeslot of(TimeslotRequest request, Fitness fitness) {
        return new Timeslot(request, fitness);
    }

    public void increaseCapacity() {
        capacity++;
    }

    public void decreaseCapacity() {
        capacity--;
    }
}
