package com.sparta.fitnus.ssenotification.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.entity.SseNotification;
import com.sparta.fitnus.ssenotification.repository.EmitterRepository;
import com.sparta.fitnus.ssenotification.repository.NotificationRepository;
import java.time.LocalDateTime;
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
}
