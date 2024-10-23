package com.sparta.fitnus.calender.entity;

import com.sparta.fitnus.timeslot.entity.TimeSlot;
import com.sparta.fitnus.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Calender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long calender_id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User users;


    @ManyToMany(mappedBy = "calenders")
    private List<TimeSlot> timeSlots = new ArrayList<>();

}
