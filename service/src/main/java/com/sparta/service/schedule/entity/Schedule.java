package com.sparta.service.schedule.entity;

import com.sparta.service.club.entity.Club;
import com.sparta.service.timeslot.entity.Timeslot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "timeslot_id")
    private Long timeslotId;

    @Column(name = "club_id")
    private Long clubId;

    @Column(name = "schedule_name")
    private String scheduleName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "required_coupon")
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
