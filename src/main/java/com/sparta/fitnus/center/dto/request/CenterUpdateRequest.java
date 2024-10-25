package com.sparta.fitnus.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CenterUpdateRequest {
    private String nickName;
    private String centerName;
    private int price;
    private Integer openTime;
    private Integer closeTime;
    private int maxCapacity;
    private int availableCapacity;


}
