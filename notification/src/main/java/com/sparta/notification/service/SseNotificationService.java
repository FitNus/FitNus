package com.sparta.notification.service;

import com.sparta.notification.entity.SseMessageName;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

public interface SseNotificationService {
    SseEmitter subscribe(Long userId);
    void broadcast(SseMessageName name, Long userId, String eventType, String message, LocalDateTime timeStamp);
}
