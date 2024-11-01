package com.sparta.modulecommon.fitness.dto.response;

import com.sparta.modulecommon.fitness.entity.Fitness;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class FitnessResponse {
    private final String fitnessName;
    private final int requiredCoupon;

    public FitnessResponse(Fitness fitness) {
        this.fitnessName = fitness.getFitnessName();
        this.requiredCoupon = fitness.getRequiredCoupon();
    }
}
