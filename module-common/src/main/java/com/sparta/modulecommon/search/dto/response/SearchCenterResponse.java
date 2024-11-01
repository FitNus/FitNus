package com.sparta.modulecommon.search.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class SearchCenterResponse {

    private final Long centerId;
    private final String centerName;
    private final String fitnessName;

    @QueryProjection
    public SearchCenterResponse(Long centerId, String centerName, String fitnessName) {
        this.centerId = centerId;
        this.centerName = centerName;
        this.fitnessName = fitnessName;
    }
}
