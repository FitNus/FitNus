package com.sparta.notification.kafka;

import com.sparta.notification.entity.SseMessageName;
import com.sparta.notification.repository.NotificationRepository;
import com.sparta.notification.service.SseNotificationServiceImpl;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final SseNotificationServiceImpl sseNotificationService;
    private final NotificationRepository notificationRepository;

    @KafkaListener(
        topics = "notification", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNotification(Map<String, Object> message) {
        log.info("Received message: {}", message);

        String type = (String) message.get("type");
        Long userId = Long.parseLong(message.get("userId").toString());
        String notificationMessage = (String) message.get("message");

        switch (type){
            case "SCHEDULE_NOTIFICATION" -> handleScheduleNotification(userId, notificationMessage);
            case "CLUB_NOTIFICATION" -> handleClubNotification(userId, notificationMessage);
            default -> throw new IllegalArgumentException("Unknown notification type : " + type);
        }
    }

    private void handleScheduleNotification(Long userId, String message) {
        // 동일한 메시지의 알림이 이미 존재하는지 확인
        boolean exists = notificationRepository.existsByUserIdAndMessage(userId, message);
        if (exists) {
            log.warn("Duplicate notification detected: {}", message);
            return;
        }
        sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, "스케줄 알림", message, LocalDateTime.now());
    }

    private void handleClubNotification(Long userId, String message) {
        sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, "모임 신청 알림", message, LocalDateTime.now());
    }
}
