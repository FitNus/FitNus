package com.sparta.batch.entity;

import com.sparta.batch.dto.SettlementResult;
import com.sparta.common.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "settlement")
public class Settlement extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "center_id")
    private Long centerId;

    @Column(name = "center_name")
    private String centerName;

    @Column(name = "amount_to_pay")
    private Double amountToPay;

    @Column(name = "sum_of_commission")
    private Double sumOfCommission;

    private Settlement(SettlementResult settlementResult) {
        this.centerId = settlementResult.getCenterId();
        this.centerName = settlementResult.getCenterName();
        this.amountToPay = settlementResult.getSumOfRevenue();
        this.sumOfCommission = settlementResult.getSumOfCommission();
    }

    public static Settlement of(SettlementResult settlementResult) {
        return new Settlement(settlementResult);
    }
}
