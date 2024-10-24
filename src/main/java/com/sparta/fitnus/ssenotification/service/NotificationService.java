package com.sparta.fitnus.ssenotification.service;

import com.sparta.fitnus.common.exception.SseNotWorkingException;
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
public class NotificationService {
    private static final Long  DEFAULT_TIMEOUT = 60 * 1000 * 60L;
    private final EmitterRepository emitterRepository;

    /**
     * 클라이언트가 알림을 구독할 때 호출
     */
    public SseEmitter subscribe(Long userId, String lastEventId) {
        // SSE 유효 시간이 만료되면, 클라이언트에서 다시 서버로 이벤트 구독을 시도한다.
        SseEmitter sseEmitter = emitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));

        // 사용자에게 모든 데이터가 전송되었다면 emitter 삭제
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(userId));

        // Emitter 유효 시간이 만료되면 emitter 삭제
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(userId));

        // 첫 데이터를 전송하여 503 오류를 방지
        try {
            sseEmitter.send(SseEmitter.event()
                .id("")
                .name("connection")
                .data("Connection established, userId: " + userId));  // 첫 데이터 전송
        } catch (IOException e) {
            emitterRepository.deleteById(userId);
            throw new SseNotWorkingException("SSE 연결 실패: 서버와의 연결을 설정하지 못했습니다.");
        }

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
        }, 0, 30, TimeUnit.SECONDS);  // 30초마다 keep-alive 메시지 전송

        return sseEmitter;
    }

    /**
     * 이벤트가 구독되어 있는 클라이언트에게 데이터를 전송
     */
    public void broadcast(Long userId, EventPayload eventPayload) {
        sendToClient(userId, eventPayload);
    }

    private void sendToClient(Long userId, Object data) {
        SseEmitter sseEmitter = emitterRepository.findById(userId);

        if (sseEmitter != null) {
            try {
                sseEmitter.send(
                    SseEmitter.event()
                        .id(userId.toString())
                        .name("알림")
                        .data(data)
                );
            } catch (IOException e) {
                emitterRepository.deleteById(userId);
                throw new RuntimeException("연결 오류 발생");
            }
        } else {
            throw new RuntimeException("해당 사용자의 SSE 연결이 존재하지 않습니다.");
        }
    }

//    public void send(Long userId, Long notificationId){
//        emitterRepository.get(userId).ifpresentOrElse(SseEmitter ->{
//            try {
//                sseEmitter.send
//            }
//        })
//    }
}
