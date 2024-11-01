package com.sparta.modulebatch.entity;

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
    private Long centId;

    @Column(name = "amount_to_pay")
    private Integer amountToPay;
}
