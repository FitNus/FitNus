package com.sparta.fitnus.schedule.entity;

import com.sparta.fitnus.club.entity.Club;
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

    @Column(unique = true)
    private Long clubId;

    private String scheduleName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer requiredCoupon;

    private Schedule(long userId, Timeslot timeslot) {
        this.userId = userId;
        timeslotId = timeslot.getId();
        scheduleName = timeslot.getFitness().getFitnessName();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        requiredCoupon = timeslot.getFitness().getRequiredCoupon();
    }

    private Schedule(long userId, Club club) {
        this.userId = userId;
        clubId = club.getId();
        scheduleName = club.getClubName();
        startTime = club.getDate();
        requiredCoupon = 0;
    }

    public static Schedule ofTimeslot(long userId, Timeslot timeslot) {
        return new Schedule(userId, timeslot);
    }

    public static Schedule ofClub(long userId, Club club) {
        return new Schedule(userId, club);
    }

    public void updateFitnessSchedule(Timeslot timeslot) {
        timeslotId = timeslot.getId();
        scheduleName = timeslot.getFitness().getFitnessName();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        requiredCoupon = timeslot.getFitness().getRequiredCoupon();
    }

    public void updateClubSchedule(Club club) {
        clubId = club.getId();
        scheduleName = club.getClubName();
        startTime = club.getDate();
        requiredCoupon = 0;
    }
}
