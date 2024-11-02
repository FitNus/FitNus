package com.sparta.modulecommon.settlement.dto;

import lombok.Getter;

@Getter
public class SettlementResult {

    private final Long centerId;
    private final Long sumOfCoupon;

    public SettlementResult(long centerId, Long sumOfCoupon) {
        this.centerId = centerId;
        this.sumOfCoupon = sumOfCoupon;
    }
}
