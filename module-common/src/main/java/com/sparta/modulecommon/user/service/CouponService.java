package com.sparta.modulecommon.user.service;

import com.sparta.modulecommon.user.entity.User;
import com.sparta.modulecommon.user.entity.UserCoupon;
import com.sparta.modulecommon.user.repository.UserCouponRepository;
import com.sparta.modulecommon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    public void addCouponToUser(Long userId, int quantity) {
        User user = userRepository.findUserById(userId);
        user.addCoupon(quantity);
        userRepository.save(user);  // 쿠폰이 추가된 유저 저장
    }


    @Transactional
    public void expireCoupons(LocalDateTime currentDateTime) {
        List<UserCoupon> expiredCoupons = userCouponRepository.findExpiredCouponsWithRemainingQuantity(currentDateTime);

        for (UserCoupon coupon : expiredCoupons) {
            coupon.expire();
        }

        userCouponRepository.saveAll(expiredCoupons); // 만료된 쿠폰을 저장하여 상태를 DB에 반영
    }

    @Transactional
    public void useCoupons(Long userId, int quantity) {
        User user = userRepository.findUserById(userId);

        int remainingQuantity = quantity;

        for (UserCoupon coupon : user.getUserCoupons()) {
            int availableCoupons = coupon.getRemainingQuantity();
            log.info("현재 쿠폰 수량: {}, 사용 요청 수량: {}", availableCoupons, remainingQuantity);
            if (availableCoupons >= remainingQuantity) {
                coupon.useCoupon(remainingQuantity);
                remainingQuantity = 0;
                break;
            } else {
                coupon.useCoupon(availableCoupons);
                remainingQuantity -= availableCoupons;
            }
        }

        if (remainingQuantity > 0) {
            throw new IllegalArgumentException("보유한 쿠폰 수량이 부족합니다. 현재 남은 수량: " + user.getTotalCoupons());
        }

        userRepository.save(user);
    }
}