package com.sparta.fitnus.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CenterUpdateRequest {
    private String centerName;
    private Integer openTime;
    private Integer closeTime;
}
