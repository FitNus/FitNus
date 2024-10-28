package com.sparta.fitnus.search.dto.response;

import com.sparta.fitnus.center.entity.Center;
import lombok.Getter;

@Getter
public class SearchCenterResponse {

    private final Long centerId;
    private final String centerName;

    public SearchCenterResponse(Center center) {
        this.centerId = center.getId();
        this.centerName = center.getCenterName();
    }
}
