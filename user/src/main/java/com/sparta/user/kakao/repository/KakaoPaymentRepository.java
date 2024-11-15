package com.sparta.user.kakao.repository;


import com.sparta.user.kakao.entity.KakaoPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoPaymentRepository extends JpaRepository<KakaoPayment, Long> {
}

