package com.sparta.service.fitness.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FitnessRequest {
    private String fitnessName;
    private int requiredCoupon;
    private Long centerId;
}
