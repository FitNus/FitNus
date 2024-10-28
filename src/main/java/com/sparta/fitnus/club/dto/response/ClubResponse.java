package com.sparta.fitnus.club.dto.response;

import com.sparta.fitnus.club.entity.Club;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClubResponse {

    private final Long id;
    private final Long leaderId;
    private final String clubName;
    private final String clubInfo;
    private final String place;
    private final LocalDateTime date;

    public ClubResponse(Club club) {
        id = club.getId();
        leaderId = club.getLeaderId();
        clubName = club.getClubName();
        clubInfo = club.getClubInfo();
        place = club.getPlace();
        date = club.getDate();
    }
}
