package com.sparta.fitnus.ssenotification.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.ssenotification.dto.EventPayload;
import com.sparta.fitnus.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.fitnus.user.entity.AuthUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/sse/subscribe/users", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal AuthUser authUser){
        return sseNotificationServiceImpl.subscribe(authUser.getId());
    }

    /**
     * 사용자의 알림 목록을 조회합니다. `type` 파라미터에 따라 읽지 않은 알림 또는 전체 알림을 반환
     * @param authUser 현재 인증된 사용자 정보 (ID 포함)
     * @param type 알림 조회 유형을 지정하는 문자열 (읽지 않은 알림: "unread", 전체 알림: "all")
     * @return ApiResponse<List < EventPayload>> 지정된 유형에 맞는 알림 목록을 포함한 응답 객체
     */
    @GetMapping("/notifications")
    public ApiResponse<List<EventPayload>> getNotifications(
        @AuthenticationPrincipal AuthUser authUser,
        @RequestParam String type,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page-1, size);
        // type 값 검증 및 알림 목록 조회
        List<EventPayload> notifications = sseNotificationServiceImpl.getNotifications(authUser.getId(), type, pageable);
        return ApiResponse.createSuccess(notifications);
    }

    /**
     * 특정 알림을 읽음 처리하는 엔드포인트
     * @param id 읽음 처리할 알림의 ID
     * @return 성공 응답 메시지
     */
    @PatchMapping("/notifications/{id}/read")
    public ApiResponse<String> markAsRead(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long id) {
        return ApiResponse.createSuccess(sseNotificationServiceImpl.markAsRead(authUser.getId(), id));
    }
}

