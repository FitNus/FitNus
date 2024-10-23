package com.sparta.fitnus.calender.entity;

import com.sparta.fitnus.timeslot.entity.TimeSlot;
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
    private Long id;

    @ManyToMany(mappedBy = "calenders")
    private List<TimeSlot> timeSlots = new ArrayList<>();

}
