package com.sparta.modulecommon.kakao.service;

import com.sparta.modulecommon.kakao.dto.request.KakaoPayApproveRequest;
import com.sparta.modulecommon.kakao.dto.request.KakaoPayReadyRequest;
import com.sparta.modulecommon.kakao.dto.response.KakaoPayApproveResponse;
import com.sparta.modulecommon.kakao.dto.response.KakaoPayReadyResponse;
import com.sparta.modulecommon.kakao.entity.KakaoPayment;
import com.sparta.modulecommon.kakao.repository.KakaoPaymentRepository;
import com.sparta.modulecommon.user.entity.User;
import com.sparta.modulecommon.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoPayService {
    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String KAKAO_PAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";

    private static final String AUTHORIZATION_KEY = "SECRET_KEY DEV6A131CBDCB515CBADC4CB66E22E29B6057E21";

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final KakaoPaymentRepository kakaoPaymentRepository;

    public KakaoPayReadyResponse payReady(int quantity, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION_KEY);
        headers.add("Content-Type", "application/json");

        KakaoPayReadyRequest readyRequest = new KakaoPayReadyRequest(quantity, String.valueOf(userId));

        HttpEntity<KakaoPayReadyRequest> requestEntity = new HttpEntity<>(readyRequest, headers);
        ResponseEntity<KakaoPayReadyResponse> response = restTemplate.postForEntity(KAKAO_PAY_READY_URL, requestEntity, KakaoPayReadyResponse.class);

        return response.getBody();
    }

    public KakaoPayApproveResponse payApprove(String tid, String pgToken, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION_KEY);
        headers.add("Content-Type", "application/json");

        // userId를 partnerUserId로 사용
        KakaoPayApproveRequest approveRequest = new KakaoPayApproveRequest(tid, pgToken, String.valueOf(userId));
        HttpEntity<KakaoPayApproveRequest> requestEntity = new HttpEntity<>(approveRequest, headers);
        ResponseEntity<KakaoPayApproveResponse> response = restTemplate.postForEntity(KAKAO_PAY_APPROVE_URL, requestEntity, KakaoPayApproveResponse.class);

        KakaoPayApproveResponse approveResponse = response.getBody();

        // 응답이 null이 아닌 경우에만 처리
        if (approveResponse != null) {
            log.info("approveResponse: " + approveResponse.toString());
            log.info("itemName : " + approveResponse.getItemName());
            log.info("amount : " + approveResponse.getAmount().getTotal());

            User user = userRepository.findUserById(userId);

            // KakaoPayment 엔티티에 결제 정보를 저장
            KakaoPayment payment = new KakaoPayment(
                    user,
                    tid,
                    approveResponse.getAmount().getTotal(),    // 총 결제 금액
                    "COMPLETED",
                    approveResponse.getQuantity()
            );
            kakaoPaymentRepository.save(payment);
        } else {
            log.error("KakaoPay approve response is null.");
            throw new IllegalStateException("Failed to approve KakaoPay payment");
        }

        return approveResponse;
    }
}



