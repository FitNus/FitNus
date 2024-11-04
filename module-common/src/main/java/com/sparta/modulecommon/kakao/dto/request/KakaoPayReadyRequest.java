package com.sparta.modulecommon.kakao.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoPayReadyRequest {
    private final String cid = "TC0ONETIME";

    @JsonProperty("partner_order_id")
    private final String partnerOrderId = "FitNus";

    @JsonProperty("partner_user_id")
    private final String partnerUserId;

    @JsonProperty("item_name")
    private final String itemName;

    private final int quantity = 1;

    @JsonProperty("total_amount")
    private final int totalAmount;

    @JsonProperty("vat_amount")
    private final int vatAmount = 0;//부과세 금액

    @JsonProperty("tax_free_amount")
    private final int taxFreeAmount = 0;//비과세 금액

    @JsonProperty("approval_url")
    private final String approvalUrl = "http://localhost:8080/api/v1/kakaopay/completed";

    @JsonProperty("fail_url")
    private final String failUrl = "http://localhost:8080/kakaopay/fail";

    @JsonProperty("cancel_url")
    private final String cancelUrl = "http://localhost:8080/kakaopay/cancel";

    public KakaoPayReadyRequest(String itemName, int totalAmount, String userId) {
        this.partnerUserId = userId;
        this.itemName = itemName;
        this.totalAmount = totalAmount;
    }
}
