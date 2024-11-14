package com.sparta.modulecommon.auction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BidRequest {
    @NotNull(message = "입찰 금액은 필수입니다.")
    @Min(value = 1, message = "입찰 금액은 0보다 커야 합니다.")
    private int bidAmount;
}
