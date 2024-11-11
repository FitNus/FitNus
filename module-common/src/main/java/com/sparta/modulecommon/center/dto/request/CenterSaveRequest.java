package com.sparta.modulecommon.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CenterSaveRequest {
    private String centerName;
    private String address;
    private Integer openTime;
    private Integer closeTime;
}
