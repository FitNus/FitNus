package com.sparta.fitnus.fitness.dto.response;

import com.sparta.fitnus.fitness.entity.Fitness;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class FitnessResponse {
    private final String fitnessName;
    private final int price;

    public FitnessResponse(Fitness fitness) {
        this.fitnessName = fitness.getFitnessName();
        this.price = fitness.getPrice();
    }
}
