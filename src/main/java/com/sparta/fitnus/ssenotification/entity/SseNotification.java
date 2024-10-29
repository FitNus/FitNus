package com.sparta.fitnus.ssenotification.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "notifications")
@NoArgsConstructor
public class SseNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String eventType;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead; // 읽음 상태

    public SseNotification(Long userId, String eventType, String message, LocalDateTime timestamp) {
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
