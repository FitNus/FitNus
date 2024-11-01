package com.sparta.modulecommon.schedule.service;

import com.sparta.modulecommon.common.exception.DuplicateNotificationException;
import com.sparta.modulecommon.ssenotification.entity.SseMessageName;
import com.sparta.modulecommon.ssenotification.repository.NotificationRepository;
import com.sparta.modulecommon.ssenotification.service.SseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ScheduleMessageService {

    private final TaskScheduler taskScheduler;
    private final SseNotificationService sseNotificationService;
    private final NotificationRepository notificationRepository;

    /**
     * 일정 알림을 시작 1시간 전에 예약하는 메서드.
     *
     * @param userId    사용자 ID
     * @param startTime 일정 시작 시간
     */
    public void scheduleNotification(Long userId, LocalDateTime startTime) {
        LocalDateTime notificationTime = startTime.minusHours(1);  // 시작 시간 1시간 전 알림
        Date notificationDate = Date.from(notificationTime.atZone(ZoneId.systemDefault()).toInstant());

        // 스케줄링된 작업: 알림 전송
        taskScheduler.schedule(() -> sendNotification(userId, startTime), notificationDate);
    }

    /**
     * 실제 알림 전송 메서드
     *
     * @param userId    사용자 ID
     * @param startTime 일정 시작 시간
     */
    private void sendNotification(Long userId, LocalDateTime startTime) {
        String message = "일정 시작 1시간 전입니다. 예약된 시간: " + startTime;

        // 동일한 메시지의 알림이 이미 존재하는지 확인
        boolean exists = notificationRepository.existsByUserIdAndMessage(userId, message);

        if (exists) {
            // 이미 존재하는 알림에 대한 예외 처리
            throw new DuplicateNotificationException("이미 존재하는 알림입니다.");
        }

        // 알림을 저장하고 전송
        sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, "일정 알림", message, LocalDateTime.now());
    }
}
