package com.sparta.modulecommon.settlement.entity;

import com.sparta.modulecommon.settlement.dto.SettlementResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "center_id")
    private Long centerId;

    @Column(name = "amount_to_pay")
    private Long amountToPay;

    private Result(SettlementResult settlementResult) {
        this.centerId = settlementResult.getCenterId();
        amountToPay = settlementResult.getSumOfCoupon();
    }

    public static Result of(SettlementResult settlementResult) {
        return new Result(settlementResult);
    }
}
