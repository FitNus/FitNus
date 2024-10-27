package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.repository.EmitterRepository;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseNotificationServiceImpl implements SseNotificationService {
    private static final Long  DEFAULT_TIMEOUT = 30 * 1000 * 60L;
    private static final long RECONNECTION_TIMEOUT = 0L;
    private final EmitterRepository emitterRepository;

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
        sendToClient(userId,"subscribe event, userId : " + userId);

        // 주기적으로 keep-alive 메시지를 전송하여 연결 유지
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                sseEmitter.send(SseEmitter.event()
                    .name("keep-alive")
                    .data("keep-alive"));  // keep-alive 메시지 전송
            } catch (IOException e) {
                emitterRepository.deleteById(userId);  // 연결 실패 시 emitter 삭제
                scheduler.shutdown();  // 스케줄러 종료
            }
        }, 0, 60, TimeUnit.SECONDS);  // 30초마다 keep-alive 메시지 전송

        return sseEmitter;
    }

    /**
     * 구독된 사용자에게 알림을 전송하는 메서드
     * @param userId 알림을 받을 사용자 ID
     * @param eventPayload 전송할 알림의 내용
     */
    public void broadcast(Long userId, EventPayload eventPayload) {
        sendToClient(userId, eventPayload);
    }

    /**
     * 클라이언트에게 데이터를 전송하는 메서드
     * @param userId 데이터를 전송할 사용자 ID
     * @param data 전송할 데이터 (알림 또는 메시지)
     */
    private void sendToClient(Long userId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);

        if (sseEmitter == null) {
            return; // 연결된 Emitter가 없으면 작업 중지
        }

        try {
            sseEmitter.send(
                SseEmitter.event()
                    .id(userId.toString())
                    .name("알림")
                    .data(data)
                    .reconnectTime(RECONNECTION_TIMEOUT)//재연결 대기 시간 설정
            );
        } catch (IOException e) {
            // 전송 실패 시 Emitter 삭제 및 재시도 로직 실행
            emitterRepository.deleteById(userId);
            retrySendToClient(userId, data);
            throw new RuntimeException("연결 오류 발생");
        }
    }

    /**
     * 데이터 전송이 실패했을 때, 재시도하는 메서드
     * @param userId 데이터를 전송할 사용자 ID
     * @param data 전송할 데이터 (알림 또는 메시지)
     */
    private void retrySendToClient(Long userId, Object data) {
        int maxRetries = 3;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                Thread.sleep(2000); // 2초 대기 후 재시도
                sendToClient(userId, data);
                return; // 성공 시 종료
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attempt++;
        }
        throw new RuntimeException("연결 오류 발생: 알림 전송에 실패했습니다.");
    }

    /**
     * 특정 사용자의 SSE 연결을 강제로 종료하는 메서드
     * @param userId 연결을 종료할 사용자 ID
     */
    public void delete(Long userId) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);
        if (sseEmitter != null) {
            sseEmitter.complete(); // 연결 종료
            emitterRepository.deleteById(userId); // Emitter 삭제
        }
    }
}
