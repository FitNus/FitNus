package com.sparta.modulecommon.ssenotification.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.modulecommon.ssenotification.entity.SseMessageName;
import com.sparta.modulecommon.ssenotification.repository.EmitterRepository;
import com.sparta.modulecommon.ssenotification.repository.NotificationRepository;
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

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SseNotificationServiceImpl sseNotificationService;

    @Test
    void 구독_성공_테스트() {
        // given: SseEmitter가 정상적으로 생성되고 저장됨
        Long userId = 1L;
        SseEmitter sseEmitter = new SseEmitter(60000L);
        when(emitterRepository.save(any(Long.class), any(SseEmitter.class))).thenReturn(sseEmitter);

        // when: 사용자 ID로 subscribe 메서드를 호출
        SseEmitter result = sseNotificationService.subscribe(userId);

        // then: SseEmitter가 null이 아닌지 확인
        assertNotNull(result, "SseEmitter 객체가 생성되어야 합니다.");
        verify(emitterRepository, times(1)).save(eq(userId), any(SseEmitter.class));
        verify(emitterRepository, never()).deleteById(userId);
    }

    @Test
    void 알림_전송_테스트() {
        // given
        Long userId = 1L;
        String eventType = "Test Event";
        String message = "Test notification";
        LocalDateTime timeStamp = LocalDateTime.now();
        Long savedId = 100L;
        SseEmitter sseEmitter = new SseEmitter();

        when(notificationService.save(any(Long.class), any(String.class), any(String.class), any(LocalDateTime.class)))
            .thenReturn(savedId);
        when(emitterRepository.findById(userId)).thenReturn(sseEmitter);

        // when
        sseNotificationService.broadcast(SseMessageName.FIRST_MESSAGE, userId, eventType, message, timeStamp);

        // then
        verify(notificationService, times(1)).save(eq(userId), eq(eventType), eq(message), eq(timeStamp));
        verify(emitterRepository, times(1)).findById(userId);
    }

}

