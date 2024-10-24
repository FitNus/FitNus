package com.sparta.fitnus.ssenotification.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 클라이언트의 이벤트 구독을 수락한다. text/event-stream은 SSE를 위한 Mime Type이다. 서버 -> 클라이언트로 이벤트를 보낼 수 있게된다.
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public ApiResponse<SseEmitter> subscribe(@PathVariable Long id, @RequestHeader(value = "Last-Event-Id", required = false, defaultValue = "")String lastEventId){
        return ApiResponse.createSuccess(notificationService.subscribe(id, lastEventId));
    }

    /**
     * 이벤트를 구독 중인 클라이언트에게 데이터를 전송한다.
     */
    @PostMapping("/send/{id}")
    public void broadcast(@PathVariable Long id, @RequestBody EventPayload eventPayload){
        notificationService.broadcast(id, eventPayload);
    }

}
