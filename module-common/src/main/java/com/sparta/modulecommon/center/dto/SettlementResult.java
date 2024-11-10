package com.sparta.modulecommon.center.dto;

import lombok.Getter;

@Getter
public class SettlementResult {

    private final Long centerId;
    private final String centerName;
    private final Double sumOfRevenue;
    private final Double sumOfCommission;

    public SettlementResult(long centerId, String centerName, Double sumOfRevenue, Double sumOfCommission) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.sumOfRevenue = sumOfRevenue;
        this.sumOfCommission = sumOfCommission;
    }
}
