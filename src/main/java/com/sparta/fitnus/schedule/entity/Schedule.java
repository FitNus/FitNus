package com.sparta.fitnus.schedule.entity;

import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.user.entity.User;
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

    private String fitnessName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Schedule(Timeslot timeslot, User user) {
        fitnessName = timeslot.getFitness().getFitnessName();
        startTime = timeslot.getStartTime();
        endTime = timeslot.getEndTime();
        price = timeslot.getFitness().getPrice();
        this.user = user;
    }

    public static Schedule of(Timeslot timeslot, User user) {
        return new Schedule(timeslot, user);
    }
}
