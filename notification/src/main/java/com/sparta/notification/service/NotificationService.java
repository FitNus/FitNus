package com.sparta.notification.service;

import com.sparta.common.exception.AccessDeniedException;
import com.sparta.notification.dto.EventPayload;
import com.sparta.notification.entity.SseNotification;
import com.sparta.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Long getUnreadNotificationsCount(Long userId){
        return notificationRepository.countUnreadNotifications(userId);
    }

    public Long save(Long userId, String eventType, String message, LocalDateTime timestamp){
        SseNotification notification = new SseNotification(userId, eventType, message, timestamp);
        notificationRepository.save(notification);
        return notification.getId();
    }

    /**
     * 주어진 사용자 ID에 대한 알림 목록을 조회하는 메서드.
     * @param userId 알림을 조회할 사용자의 ID
     * @param type 알림 조회 유형 ("unread" 또는 "all")
     * @return 사용자의 알림을 담고 있는 EventPayload 객체 리스트
     */
    public List<EventPayload> getNotifications(Long userId, String type, Pageable pageable) {
        Page<SseNotification> notifications;

        // type 값에 따라 알림 조회
        if ("unread".equalsIgnoreCase(type)) {
            notifications = notificationRepository.findUnreadNotificationsByUserId(userId, pageable);
        } else if ("all".equalsIgnoreCase(type)) {
            notifications = notificationRepository.findAllByUserId(userId, pageable);
        } else {
            throw new IllegalArgumentException("유효하지 않은 타입입니다. 'unread' 또는 'all' 둘 중 하나의 타입을 입력해주세요.");
        }

        // 조회된 알림을 EventPayload로 변환하여 반환
        return notifications.stream()
            .map(notification -> new EventPayload(
                notification.getId(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getTimestamp()
            ))
            .toList();
    }

    /**
     * 알림을 읽음 상태로 업데이트하는 메서드
     * @param notificationId 읽음 처리할 알림의 ID
     */
    @Transactional
    public String markAsRead(Long currentUserId, Long notificationId) {

        SseNotification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        if(!notification.getUserId().equals(currentUserId)){
            throw new AccessDeniedException("해당 알림 권한이 없습니다.");
        }

        notification.markAsRead(); // 읽음 처리
        notificationRepository.save(notification); // 업데이트된 상태로 저장

        return("알림이 읽음 처리되었습니다.");
    }

}
