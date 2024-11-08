package com.sparta.modulecommon.kakao.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderCreateForm {
    private final int quantity;

    @JsonCreator
    public OrderCreateForm(@JsonProperty("quantity") int quantity) {
        this.quantity = quantity;
    }
}

