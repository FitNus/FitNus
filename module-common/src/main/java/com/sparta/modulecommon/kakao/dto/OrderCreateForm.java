package com.sparta.modulecommon.kakao.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderCreateForm {
    private final String name;
    private final int totalPrice;
}

