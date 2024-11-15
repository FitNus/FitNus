package com.sparta.auction.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResultMessage {
    private Long auctionId;
    private Long winnerId;
    private int finalBidAmount;
}