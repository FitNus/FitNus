package com.sparta.fitnus.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CenterSaveRequest {

    private String nickName;

    private String centerName;

    private int price;

    private Integer openTime;

    private Integer closeTime;

    private int maxCapacity;

    //private int availableCapacity; // maxCapacity와 동일값으로 초반에는 자동설정됩니다.


}
