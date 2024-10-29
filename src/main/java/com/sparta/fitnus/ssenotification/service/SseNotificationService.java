package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseNotificationService {
    SseEmitter subscribe(Long userId);
    void broadcast(SseMessageName name, Long userId, EventPayload eventPayload);
    String markAsRead(Long currentUserId, Long notificationId);
}
