package com.sparta.notification.kafka;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class   NotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendClubNotification(Long userId, String message, Long clubId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "CLUB_NOTIFICATION");
        notification.put("userId", userId);
        notification.put("message", message);
        notification.put("timestamp", LocalDateTime.now().toString());
        notification.put("additionalData", Map.of("clubId", clubId));

        kafkaTemplate.send("notification", notification);
        log.info("Published to topic: {} with message: {}",notification, message);
    }

    public void sendScheduleNotification(Long userId, String message, Long scheduleId, LocalDateTime startTime) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SCHEDULE_NOTIFICATION");
        notification.put("userId", userId);
        notification.put("message", message);
        notification.put("timestamp", LocalDateTime.now().toString());
        notification.put("additionalData", Map.of("scheduleId", scheduleId, "startTime", startTime.toString()));

        kafkaTemplate.send("notification", notification);
        log.info("Published to topic: {} with message: {}",notification, message);
    }

}