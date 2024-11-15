package com.sparta.user.kakao.controller;

import com.sparta.common.dto.AuthUser;
import com.sparta.user.kakao.SessionUtils;
import com.sparta.user.kakao.dto.OrderCreateForm;
import com.sparta.user.kakao.dto.response.KakaoPayReadyResponse;
import com.sparta.user.kakao.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    @PostMapping("/v1/kakaopay/ready")
    public @ResponseBody KakaoPayReadyResponse payReady(@RequestBody OrderCreateForm orderCreateForm, @AuthenticationPrincipal AuthUser authUser) {
        int quantity = orderCreateForm.getQuantity();

        log.info("주문 수량: " + quantity);

        KakaoPayReadyResponse readyResponse = kakaoPayService.payReady(quantity, authUser.getId());
        SessionUtils.addAttribute("tid", readyResponse.getTid());
        SessionUtils.addAttribute("quantity", quantity);
        log.info("결제 고유번호: " + readyResponse.getTid());
        log.info("세션에서 가져온 tid: " + SessionUtils.getStringAttributeValue("tid"));

        return readyResponse;
    }

    @GetMapping("/v1/kakaopay/completed") // 수정된 경로
    public String payCompleted(@RequestParam("pg_token") String pgToken, @AuthenticationPrincipal AuthUser authUser) {
        String tid = SessionUtils.getStringAttributeValue("tid");
        int quantity = Integer.parseInt(SessionUtils.getStringAttributeValue("quantity"));
        log.info("결제 승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + tid);
        log.info("주문 수량: " + quantity);

        boolean isSuccessful = kakaoPayService.handlePaymentAndAddCoupon(tid, pgToken, authUser.getId(), quantity);

        if (isSuccessful) {
            return "redirect:/api/order/completed";
        } else {
            return "redirect:/order/failed";
        }
    }

    @GetMapping("/order/completed")
    public String orderCompleted() {
        return "redirect:/order_complete.html"; // HTML 파일로 리디렉션
    }

    @GetMapping("/order/failed")
    public String orderFailed() {
        return "redirect:/order_failed.html"; // HTML 파일로 리디렉션
    }
}
