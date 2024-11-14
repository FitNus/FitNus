package com.sparta.modulecommon.auction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;


public record AuctionRequest(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime endTime) {
}

