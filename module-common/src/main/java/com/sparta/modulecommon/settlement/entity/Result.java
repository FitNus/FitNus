package com.sparta.modulecommon.settlement.entity;

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

    @Column(name = "center_id", unique = true)
    private Long centerId;

    @Column(name = "amount_to_pay")
    private Double amountToPay;

    private Result(long centerId, double amountToPay) {
        this.centerId = centerId;
        this.amountToPay = amountToPay;
    }

    public static Result of(long centerId, double amountToPay) {
        return new Result(centerId, amountToPay);
    }
}
