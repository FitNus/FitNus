package com.sparta.fitnus.center.dto.request;

import lombok.Getter;

@Getter
public class CenterSaveRequest {

    private String nickName;

    private String centerName;

    private int price;

    private Integer openTime;

    private Integer closeTime;

    private int maxCapacity;
    private int availableCapacity;


}
