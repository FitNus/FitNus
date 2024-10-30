package com.sparta.fitnus.ssenotification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.fitnus.common.exception.AccessDeniedException;
import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.entity.SseNotification;
import com.sparta.fitnus.ssenotification.repository.EmitterRepository;
import com.sparta.fitnus.ssenotification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
public class SseNotificationTest {
    @Mock
    private EmitterRepository emitterRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private SseNotificationServiceImpl sseNotificationService;

    @Test
    void 구독_성공_테스트() {
        // given
        Long userId = 1L;
        SseEmitter sseEmitter = new SseEmitter();

        // EmitterRepository의 save 메서드가 호출될 때 SseEmitter 객체 반환 설정
        when(emitterRepository.save(eq(userId), any(SseEmitter.class))).thenReturn(sseEmitter);

        // 알림 개수 반환
        when(notificationRepository.countUnreadNotifications(userId)).thenReturn(5L);

        // when
        SseEmitter result = sseNotificationService.subscribe(userId);

        // then
        assertNotNull(result); // SseEmitter가 null이 아님을 확인
        verify(emitterRepository, times(1)).save(eq(userId), any(SseEmitter.class));
        verify(notificationRepository, times(1)).countUnreadNotifications(userId);
    }

    @Test
    void 알림_전송_테스트() {
        // given
        Long userId = 1L;
        String eventType = "Test Event";
        String message = "Test notification";
        LocalDateTime timeStamp = LocalDateTime.now();
        SseNotification notification = new SseNotification(userId, eventType, message, timeStamp);

        when(notificationRepository.save(any(SseNotification.class))).thenReturn(notification);
        SseEmitter mockEmitter = new SseEmitter();
        when(emitterRepository.findById(userId)).thenReturn(mockEmitter);

        // when
        sseNotificationService.broadcast(SseMessageName.MESSAGE, userId, eventType, message, timeStamp);

        // then
        verify(notificationRepository, times(1)).save(any(SseNotification.class));
        verify(emitterRepository, times(1)).findById(userId);
    }

    @Test
    void 읽지_않은_알림목록_조회_테스트() {
        // given
        Long userId = 1L;
        SseNotification notification1 = new SseNotification(userId, "Type1", "Message1", LocalDateTime.now());
        SseNotification notification2 = new SseNotification(userId, "Type2", "Message2", LocalDateTime.now());
        when(notificationRepository.findUnreadNotificationsByUserId(userId)).thenReturn(
            Arrays.asList(notification1, notification2));

        // when
        List<EventPayload> result = sseNotificationService.getUnreadNotifications(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("Message1", result.get(0).getMessage());
    }

    @Test
    void 알림_읽음_업데이트_테스트() {
        // given
        Long userId = 1L;
        Long notificationId = 1L;
        SseNotification notification = new SseNotification(userId, "Type1", "Message1", LocalDateTime.now());
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // when
        String result = sseNotificationService.markAsRead(userId, notificationId);

        // then
        assertEquals("알림이 읽음 처리되었습니다.", result);
        assertTrue(notification.isRead()); // 알림이 읽음 처리되었는지 확인
    }

    @Test
    void 다른_사용자_알림_읽음_예외_발생_테스트() {
        // given
        Long currentUserId = 1L;
        Long notificationOwnerId = 2L;
        Long notificationId = 1L;
        SseNotification notification = new SseNotification(notificationOwnerId, "Type1", "Message1", LocalDateTime.now());
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // when & then
        assertThrows(AccessDeniedException.class, () -> sseNotificationService.markAsRead(currentUserId, notificationId));
        verify(notificationRepository, never()).save(notification);
    }
}
