package com.sparta.modulecommon.kakao.controller;

import com.sparta.modulecommon.kakao.SessionUtils;
import com.sparta.modulecommon.kakao.dto.OrderCreateForm;
import com.sparta.modulecommon.kakao.dto.response.KakaoPayReadyResponse;
import com.sparta.modulecommon.kakao.service.KakaoPayService;
import com.sparta.modulecommon.user.entity.AuthUser;
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
        String name = orderCreateForm.getName();
        int totalPrice = orderCreateForm.getTotalPrice();

        log.info("주문 상품 이름: " + name);
        log.info("주문 금액: " + totalPrice);

        KakaoPayReadyResponse readyResponse = kakaoPayService.payReady(name, totalPrice, authUser.getId());
        SessionUtils.addAttribute("tid", readyResponse.getTid());
        log.info("결제 고유번호: " + readyResponse.getTid());
        log.info("세션에서 가져온 tid: " + SessionUtils.getStringAttributeValue("tid"));

        return readyResponse;
    }

    @GetMapping("/v1/kakaopay/completed") // 수정된 경로
    public String payCompleted(@RequestParam("pg_token") String pgToken, @AuthenticationPrincipal AuthUser authUser) {
        String tid = SessionUtils.getStringAttributeValue("tid");
        log.info("결제 승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + tid);

        kakaoPayService.payApprove(tid, pgToken, authUser.getId());
        return "redirect:/order/completed";
    }

    @GetMapping("/order/completed")
    public String orderCompleted() {
        return "redirect:order_complete.html"; // Thymeleaf를 사용하여 order_complete.html 페이지를 렌더링
    }
}



