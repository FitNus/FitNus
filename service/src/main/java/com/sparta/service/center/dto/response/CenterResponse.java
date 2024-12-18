package com.sparta.service.center.dto.response;

import com.sparta.service.center.entity.Center;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CenterResponse {

    private final Long id;
    private final String centerName;
    private final String address;
    private final Integer openTime;
    private final Integer closeTime;


    public CenterResponse(Center center) {
        this.id = center.getId();
        this.centerName = center.getCenterName();
        this.address = center.getAddress();
        this.openTime = center.getOpenTime();
        this.closeTime = center.getCloseTime();
    }

}
