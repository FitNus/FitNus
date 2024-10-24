package com.sparta.fitnus.timeslot.entity;

import com.sparta.fitnus.calender.entity.Calender;
import com.sparta.fitnus.center.entity.Center;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String fitnessName;

    private int price;
    //시작시간

    //끝나는시간

    //삭제여부

    private String maxCapacity;
    private String availableCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calender_id", nullable = false)
    private Calender calender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id", nullable = false)
    private Center center;

    @ManyToMany
    @JoinTable(
            name = "timeslot_calender",
            joinColumns = @JoinColumn(name = "timeslot_id"),
            inverseJoinColumns = @JoinColumn(name = "calender_id")
    )
    private List<Calender> calenders = new ArrayList<>();

}
