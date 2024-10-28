package com.sparta.fitnus.search.dto.response;

import com.sparta.fitnus.club.entity.Club;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SearchClubResponse {

    private final Long clubId;
    private final String clubName;
    private final String clubInfo;
    private final String place;
    private final LocalDateTime date;

    public SearchClubResponse(Club club) {
        this.clubId = club.getId();
        this.clubName = club.getClubName();
        this.clubInfo = club.getClubInfo();
        this.place = club.getPlace();
        this.date = club.getDate();
    }
}
