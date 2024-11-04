package com.sparta.modulecommon.kakao.dto.response;

import lombok.Getter;

@Getter
public class KakaoPayApproveResponse {
    private String aid;                 // 요청 고유 번호
    private String tid;                 // 결제 고유 번호
    private String cid;                 // 가맹점 코드
    private String partnerOrderId;      // 가맹점 주문번호
    private String partnerUserId;       // 가맹점 회원 id
    private String paymentMethodType;   // 결제 수단, CARD 또는 MONEY 중 하나
    private String itemName;            // 상품 이름
    private String itemCode;            // 상품 코드
    private int quantity;               // 상품 수량
    private String createdAt;           // 결제 준비 요청 시각
    private String approvedAt;          // 결제 승인 시각
    private String payload;             // 결제 승인 요청에 대해 저장한 값, 요청 시 전달된 내용
    private Amount amount;              // 결제 금액 정보

    @Getter
    public static class Amount {
        private int total;      // 총 결제 금액
        private int taxFree;    // 비과세 금액
        private int vat;        // 부가세 금액
        private int point;      // 사용한 포인트 금액
        private int discount;   // 할인 금액
    }
}
