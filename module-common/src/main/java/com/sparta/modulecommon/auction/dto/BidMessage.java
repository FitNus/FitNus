package com.sparta.modulecommon.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BidMessage {
    private Long auctionId;
    private Long bidderId;
    private int bidAmount;
}
