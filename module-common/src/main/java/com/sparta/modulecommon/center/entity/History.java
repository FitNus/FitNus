package com.sparta.modulecommon.center.entity;

import com.sparta.modulecommon.center.dto.HistoryInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "center_id")
    private Long centerId;

    @Column(name = "user_id")
    private Long userId;

    private String nickname;

    @Column(name = "fitness_name")
    private String fitnessName;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private Double revenue;

    private Double commission;

    private History(HistoryInfo historyInfo) {
        centerId = historyInfo.getCenterId();
        userId = historyInfo.getUserId();
        nickname = historyInfo.getNickname();
        fitnessName = historyInfo.getFitnessName();
        startTime = historyInfo.getStartTime();
        endTime = historyInfo.getEndTime();
        revenue = historyInfo.getRevenue();
        commission = historyInfo.getCommission();
    }

    public static History of(HistoryInfo historyInfo) {
        return new History(historyInfo);
    }
}
