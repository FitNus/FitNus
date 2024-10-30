package com.sparta.fitnus.schedule.service;

import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.repository.NotificationRepository;
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

        if (!exists) {
            // 알림을 저장하고 전송
            sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, "일정 알림", message, LocalDateTime.now());
        } else {
            System.out.println("이미 존재하는 알림입니다.");
        }
    }
}