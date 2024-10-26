package com.sparta.fitnus.fitness.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FitnessRequest {
    private String fitnessName;
    private int price;
}
