package com.sparta.fitnus.center.entity;

import com.sparta.fitnus.centerreview.entity.CenterReview;
import com.sparta.fitnus.timeslot.entity.TimeSlot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long center_id;

    //센터장 id
}
