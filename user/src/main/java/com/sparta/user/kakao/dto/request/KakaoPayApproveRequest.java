package com.sparta.user.kakao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoPayApproveRequest {
    private final String cid = "TC0ONETIME";
    private final String tid;

    @JsonProperty("partner_order_id")
    private final String partnerOrderId = "FitNus";

    @JsonProperty("partner_user_id")
    private final String partnerUserId;

    @JsonProperty("pg_token")
    private final String pgToken;

    public KakaoPayApproveRequest(String tid, String pgToken, String userId) {
        this.partnerUserId = userId;
        this.tid = tid;
        this.pgToken = pgToken;
    }
}
