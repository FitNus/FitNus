package com.sparta.modulecommon.kakao.repository;


import com.sparta.modulecommon.kakao.entity.KakaoPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoPaymentRepository extends JpaRepository<KakaoPayment, Long> {
}

