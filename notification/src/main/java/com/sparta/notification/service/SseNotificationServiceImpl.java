package com.sparta.notification.service;

import com.sparta.common.exception.SseNotWorkingException;
import com.sparta.notification.entity.SseMessageName;
import com.sparta.notification.repository.EmitterRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotificationServiceImpl implements SseNotificationService {
    private static final Long  DEFAULT_TIMEOUT = 30 * 1000 * 60L;
    private static final long RECONNECTION_TIMEOUT = 0L;
    private final EmitterRepository emitterRepository;
    private final NotificationService notificationService;

    /**
     * 특정 사용자가 SSE를 통해 서버와 연결을 수립할 때 호출되는 메서드
     * @param userId 구독할 사용자 ID
     * @return 생성된 SseEmitter 객체
     */
    public SseEmitter subscribe(Long userId) {
        // SSE 유효 시간이 만료되면, 클라이언트에서 다시 서버로 이벤트 구독을 시도한다.
        SseEmitter sseEmitter = emitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // Emitter 유효 시간이 만료되면 emitter 삭제
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(userId));

        //개수 sse로 전송
        sendToClient(SseMessageName.FIRST_MESSAGE,userId, "첫 연결입니다.");

        return sseEmitter;
    }

    /**
     * 구독된 사용자에게 알림을 전송하는 메서드
     * @param userId 알림을 받을 사용자 ID
     */
    public void broadcast(SseMessageName name, Long userId, String eventType, String message, LocalDateTime timeStamp) {

        // 알림을 데이터베이스에 저장
        Long savedId =notificationService.save(userId, eventType, message, timeStamp);

        // 기존 방식대로 SSE 전송
        sendToClient(name, userId, savedId);
    }

    /**
     * 클라이언트에게 데이터를 전송하는 메서드
     * @param userId 데이터를 전송할 사용자 ID
     * @param data 전송할 데이터 (알림 또는 메시지)
     */
    private void sendToClient(SseMessageName name, Long userId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);

        if (sseEmitter == null) {
            return; // 연결된 Emitter가 없으면 작업 중지
        }

        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .id(userId.toString())
                .name(name.toString());

            // 데이터가 Long 타입의 개수인 경우와 일반 메시지인 경우 구분
            if (data instanceof Long count) {
                eventBuilder.data(count); // 개수를 그대로 전송
            } else {
                eventBuilder.data(data); // 일반 메시지 전송
            }

            // 이벤트 전송
            sseEmitter.send(eventBuilder.reconnectTime(RECONNECTION_TIMEOUT));

        } catch (IOException e) {
            // 전송 실패 시 Emitter 삭제 및 재시도 로직 실행
            emitterRepository.deleteById(userId);
            throw new SseNotWorkingException("연결 오류 발생");
        }
    }
}
