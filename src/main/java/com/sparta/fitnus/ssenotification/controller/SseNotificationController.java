package com.sparta.fitnus.ssenotification.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SseNotificationController {

    private final SseNotificationServiceImpl sseNotificationServiceImpl;

    /**
     * 클라이언트의 이벤트 구독을 수락한다. text/event-stream은 SSE를 위한 Mime Type이다. 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
     */
    @GetMapping(value = "/sse/subscribe/users", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUser authUser){
        return sseNotificationServiceImpl.subscribe(authUser.getId());
    }

    //@RequestHeader(value = "Last-Event-Id", required = false, defaultValue = "")String lastEventId

    /**
     * 이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.
     */
    @PostMapping("/sse/send/users")
    public void broadcast(@AuthenticationPrincipal AuthUser authUser, @RequestBody EventPayload eventPayload){
        sseNotificationServiceImpl.broadcast(authUser.getId(), eventPayload);
    }

    /**
     * 특정 알림을 읽음 처리하는 엔드포인트
     * @param id 읽음 처리할 알림의 ID
     * @return 성공 응답 메시지
     */
    @PatchMapping("/notifications/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        sseNotificationServiceImpl.markAsRead(id);
        return ApiResponse.createSuccess(sseNotificationServiceImpl.markAsRead(id));
    }
}

