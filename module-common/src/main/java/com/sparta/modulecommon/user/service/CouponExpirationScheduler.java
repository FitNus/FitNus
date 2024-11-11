package com.sparta.modulecommon.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponExpirationScheduler {

    private final CouponService couponService;

    @Scheduled(cron = "0 * * * * ?")  // 매 1분마다 실행
    public void expireCouponsDaily() {
        couponService.expireCoupons(LocalDateTime.now()); // 현재 시간을 LocalDateTime으로 전달
    }
}
