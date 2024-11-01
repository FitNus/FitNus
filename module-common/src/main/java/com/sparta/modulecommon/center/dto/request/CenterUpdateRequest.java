package com.sparta.modulecommon.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterUpdateRequest {
    private String centerName;
    private Integer openTime;
    private Integer closeTime;
}
