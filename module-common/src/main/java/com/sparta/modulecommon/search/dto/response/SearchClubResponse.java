package com.sparta.modulecommon.search.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchClubResponse {

    private final Long clubId;
    private final String clubName;
    private final String clubInfo;
    private final String place;
    private final LocalDateTime date;

    @QueryProjection
    public SearchClubResponse(Long clubId, String clubName, String clubInfo, String place,
            LocalDateTime date) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.clubInfo = clubInfo;
        this.place = place;
        this.date = date;
    }
}
