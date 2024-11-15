package com.sparta.user.kakao.dto.response;

import lombok.Getter;

@Getter
public class KakaoPayReadyResponse {
    private String tid;
    private String next_redirect_pc_url;
}


