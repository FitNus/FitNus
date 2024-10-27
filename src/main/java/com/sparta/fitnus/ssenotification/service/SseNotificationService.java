package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.ssenotification.dto.EventPayload;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseNotificationService {
    SseEmitter subscribe(Long userId);
    void broadcast(Long userId, EventPayload eventPayload);
    void delete(Long userId);
}
