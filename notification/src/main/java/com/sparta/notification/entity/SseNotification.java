package com.sparta.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity(name = "notifications")
@NoArgsConstructor
@Table(name = "notifications")
public class SseNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "event_type")
    private String eventType;

    private String message;

    private LocalDateTime timestamp;

    @Column(name = "is_read")
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
