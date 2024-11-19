package com.sparta.service.schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleMessageService {


    private final NotificationProducer notificationProducer;
    private final TaskScheduler taskScheduler;
    private static final String NOTIFICATION_TOPIC = "notification";

    // 예약된 알림을 관리하기 위한 Map (scheduleId를 키로 사용)
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 일정 알림을 시작 1시간 전에 예약하는 메서드.
     *
     * @param userId     사용자 ID
     * @param startTime  일정 시작 시간
     * @param scheduleId 일정 ID
     */
    public void scheduleNotification(Long userId, LocalDateTime startTime, Long scheduleId) {
        // 기존 알림이 존재하면 취소
        cancelScheduledNotification(scheduleId);

        LocalDateTime notificationTime = startTime.minusHours(1);
        String message = "일정 시작 1시간 전입니다. 예약된 시간: " + startTime;
        System.out.println("message = " + message);

        // 예약 시간 계산
        Date notificationDate = Date.from(notificationTime.atZone(ZoneId.systemDefault()).toInstant());

        // TaskScheduler를 사용해 1시간 전에 Kafka 메시지 발행 예약
        taskScheduler.schedule(() -> {
            notificationProducer.sendScheduleNotification(userId, message, scheduleId, startTime);
        }, notificationDate);

        // 새로운 예약을 Map에 저장
//        scheduledTasks.put(scheduleId, scheduledTask);
        log.info("Notification scheduled at: {}", notificationTime);
    }

    /**
     * 기존 예약된 알림을 취소하는 메서드.
     *
     * @param scheduleId 일정 ID
     */
    public void cancelScheduledNotification(Long scheduleId) {
        // 기존 예약된 Task가 있으면 취소
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(scheduleId);
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
            log.info("Existing notification cancelled for scheduleId: {}", scheduleId);
        }
    }

}