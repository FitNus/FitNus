package com.sparta.modulecommon.schedule.entity;

import com.sparta.modulecommon.club.entity.Club;
import com.sparta.modulecommon.timeslot.entity.Timeslot;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private Long timeslotId;

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

    private Schedule(Schedule schedule, LocalDateTime newStartTime) {
        userId = schedule.getUserId();
        timeslotId = schedule.getTimeslotId();
        scheduleName = schedule.getScheduleName();
        startTime = newStartTime;
        endTime = schedule.getEndTime();
        requiredCoupon = schedule.getRequiredCoupon();
    }

    public static Schedule ofTimeslot(long userId, Timeslot timeslot) {
        return new Schedule(userId, timeslot);
    }

    public static Schedule ofClub(long userId, Club club) {
        return new Schedule(userId, club);
    }

    public static Schedule fromOldSchedule(Schedule schedule, LocalDateTime newStartTime) {
        return new Schedule(schedule, newStartTime);
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
