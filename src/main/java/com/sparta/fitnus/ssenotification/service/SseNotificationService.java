package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import java.time.LocalDateTime;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseNotificationService {
    SseEmitter subscribe(Long userId);
    void broadcast(SseMessageName name, Long userId, String eventType, String message, LocalDateTime timeStamp);
    String markAsRead(Long currentUserId, Long notificationId);
}
