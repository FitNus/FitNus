package com.sparta.fitnus.fitness.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FitnessRequest {
    // fitnessName만 직접입력 받는걸로 만들고, price, capacity컬럼들은 Center에서 기입한 값으로 불러오기.
    private String fitnessName;

}
