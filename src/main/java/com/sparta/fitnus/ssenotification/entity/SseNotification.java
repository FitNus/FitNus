package com.sparta.fitnus.ssenotification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class SseNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String eventType;
    private String message;
    private LocalDate timestamp;
    private boolean isRead; // 읽음 상태

    public SseNotification(Long userId, String eventType, String message, LocalDate timestamp) {
        this.userId = userId;
        this.eventType = eventType;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false; // 기본값으로 읽음 상태는 false
    }

    // 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }

}
