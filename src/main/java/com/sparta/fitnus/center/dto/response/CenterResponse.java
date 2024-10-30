package com.sparta.fitnus.center.dto.response;

import com.sparta.fitnus.center.entity.Center;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CenterResponse {

    private final Long id;
    private final String centerName;
    private final Integer openTime;
    private final Integer closeTime;


    public CenterResponse(Center center) {
        this.id = center.getId();
        this.centerName = center.getCenterName();
        this.openTime = center.getOpenTime();
        this.closeTime = center.getCloseTime();
    }

}
