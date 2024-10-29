package com.sparta.fitnus.schedule.entity;

import com.sparta.fitnus.timeslot.entity.Timeslot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(unique = true)
    private Long timeslotId;

    private String fitnessName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer requiredCoupon;

    private Schedule(long userId, Timeslot timeslot) {
        this.userId = userId;
        timeslotId = timeslot.getId();
        fitnessName = timeslot.getFitness().getFitnessName();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        requiredCoupon = timeslot.getFitness().getRequiredCoupon();
    }

    public static Schedule of(long userId, Timeslot timeslot) {
        return new Schedule(userId, timeslot);
    }

    public void updateSchedule(Timeslot timeslot) {
        timeslotId = timeslot.getId();
        fitnessName = timeslot.getFitness().getFitnessName();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        requiredCoupon = timeslot.getFitness().getRequiredCoupon();
    }
}
