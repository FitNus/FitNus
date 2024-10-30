package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.common.exception.AccessDeniedException;
import com.sparta.fitnus.common.exception.SseNotWorkingException;
import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.entity.SseNotification;
import com.sparta.fitnus.ssenotification.repository.EmitterRepository;
import com.sparta.fitnus.ssenotification.repository.NotificationRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotificationServiceImpl implements SseNotificationService {
    private static final Long  DEFAULT_TIMEOUT = 30 * 1000 * 60L;
    private static final long RECONNECTION_TIMEOUT = 0L;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

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

        // 첫 데이터를 전송하여 503 오류를 방지
        sendToClient(SseMessageName.TEST_MESSAGE, userId,"subscribe event, userId : " + userId);

        // 읽지 않은 알림 개수 조회
        Long unreadCount = notificationRepository.countUnreadNotifications(userId);

        //개수 sse로 전송
        sendToClient(SseMessageName.FIRST_MESSAGE,userId, unreadCount);

        return sseEmitter;
    }

    /**
     * 구독된 사용자에게 알림을 전송하는 메서드
     * @param userId 알림을 받을 사용자 ID
     */
    public void broadcast(SseMessageName name, Long userId, String eventType, String message, LocalDateTime timeStamp) {

        // 알림을 데이터베이스에 저장
        SseNotification notification = new SseNotification(
            userId, eventType, message, timeStamp);

       notificationRepository.save(notification);

        // 기존 방식대로 SSE 전송
        sendToClient(name, userId, 1L);
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
            retrySendToClient(name, userId, data);
            throw new SseNotWorkingException("연결 오류 발생");
        }
    }

    /**
     * 데이터 전송이 실패했을 때, 재시도하는 메서드
     * @param userId 데이터를 전송할 사용자 ID
     * @param data 전송할 데이터 (알림 또는 메시지)
     */
    private void retrySendToClient(SseMessageName name, Long userId, Object data) {
        int maxRetries = 3;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                Thread.sleep(2000); // 2초 대기 후 재시도
                sendToClient(name, userId, data);
                return; // 성공 시 종료
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attempt++;
        }
        throw new SseNotWorkingException("연결 오류 발생: 알림 전송에 실패했습니다.");
    }

    /**
     * 주어진 사용자 ID에 대한 읽지 않은 알림 목록을 조회하는 메서드.
     * @param userId 읽지 않은 알림을 조회할 사용자의 ID
     * @return 사용자의 읽지 않은 알림을 담고 있는 EventPayload 객체 리스트
     */
    public List<EventPayload> getUnreadNotifications(Long userId) {
        List<SseNotification> unreadNotifications = notificationRepository.findUnreadNotificationsByUserId(userId);
        return unreadNotifications.stream()
            .map(notification -> new EventPayload(
                notification.getId(),
                notification.getEventType(),
                notification.getMessage(),
                notification.getTimestamp()
            ))
            .toList();
    }

    /**
     * 주어진 사용자 ID에 대한 전체 알림 목록을 조회하는 메서드.
     * @param userId 전체 알림을 조회할 사용자의 ID
     * @return 사용자의 전체 알림을 담고 있는 EventPayload 객체 리스트
     */
    public List<EventPayload> getAllNotifications(Long userId){
        List<SseNotification> allNotifications = notificationRepository.findAllByUserId(userId);
        return allNotifications.stream()
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
