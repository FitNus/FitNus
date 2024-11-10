package com.sparta.modulecommon.center.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HistoryInfo {

    private final Long centerId;

    private final Long userId;

    private final String nickname;

    private final String fitnessName;

    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final Double revenue;

    private final Double commission;

    public HistoryInfo(long centerId, long userId, String nickname, String fitnessName,
                       LocalDateTime startTime, LocalDateTime endTime, int requiredCoupon) {
        this.centerId = centerId;
        this.userId = userId;
        this.nickname = nickname;
        this.fitnessName = fitnessName;
        this.startTime = startTime;
        this.endTime = endTime;
        revenue = requiredCoupon * 1000 * 0.95;
        commission = requiredCoupon * 1000 * 0.05;
    }
}
