package com.sparta.fitnus.schedule.service;

import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.service.SseNotificationService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleMessageService {

    private final TaskScheduler taskScheduler;
    private final SseNotificationService sseNotificationService;

    // 알림 예약 메서드
    public void scheduleNotification(Long userId, LocalDateTime startTime, Long scheduleId) {
        LocalDateTime notificationTime = startTime.minusHours(1);  // 시작 시간 1시간 전 알림
        Date notificationDate = Date.from(notificationTime.atZone(ZoneId.systemDefault()).toInstant());

        // 스케줄링된 작업
        taskScheduler.schedule(() -> sendNotification(userId, scheduleId, startTime), notificationDate);
    }

    // 알림 전송 메서드
    private void sendNotification(Long userId, Long scheduleId, LocalDateTime startTime) {
        String message = "일정 시작 1시간 전입니다. 예약된 시간: " + startTime;
        sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, new EventPayload("일정 알림", message, LocalDateTime.now()));
    }
}

