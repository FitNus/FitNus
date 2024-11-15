package com.sparta.service.schedule.service;

import com.sparta.notification.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleMessageService {


    private final NotificationProducer notificationProducer;
    private final TaskScheduler taskScheduler;
    private static final String NOTIFICATION_TOPIC = "notification";

    /**
     * 일정 알림을 시작 1시간 전에 예약하는 메서드.
     *
     * @param userId    사용자 ID
     * @param startTime 일정 시작 시간
     */
    public void scheduleNotification(Long userId, LocalDateTime startTime, Long scheduleId) {
        LocalDateTime notificationTime = startTime.minusHours(1);
        String message = "일정 시작 1시간 전입니다. 예약된 시간: " + startTime;

        // 예약 시간 계산
        Date notificationDate = Date.from(notificationTime.atZone(ZoneId.systemDefault()).toInstant());

        // TaskScheduler를 사용해 1시간 전에 Kafka 메시지 발행 예약
        taskScheduler.schedule(() -> {
            notificationProducer.sendScheduleNotification(userId, message, scheduleId, startTime);
        }, notificationDate);

        log.info("Notification scheduled at: {}", notificationTime);
    }
}