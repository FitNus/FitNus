package com.sparta.fitnus.fitness.dto.response;

import com.sparta.fitnus.fitness.entity.Fitness;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FitnessResponse {
    private final String fitnessName;

    public FitnessResponse(Fitness fitness) {
        this.fitnessName = fitness.getFitnessName();
    }
}
