package com.sparta.fitnus.ssenotification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class EmitterRepository {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId, SseEmitter sseEmitter) {
        emitterMap.put(userId, sseEmitter);
        return emitterMap.get(userId);
    }

    public void deleteById(Long userId) {
        emitterMap.remove(userId);
    }

    public SseEmitter findById(Long userId) {
        return emitterMap.get(userId);
    }

}
