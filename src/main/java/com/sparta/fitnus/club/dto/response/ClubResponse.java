package com.sparta.fitnus.club.dto.response;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.user.dto.response.UserResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClubResponse {

    private final Long id;
    private final String clubName;
    private final String clubInfo;
    private final String place;
    private final LocalDateTime date;
    private final UserResponse leader;

    public ClubResponse(Club club) {
        id = club.getId();
        clubName = club.getClubName();
        clubInfo = club.getClubInfo();
        place = club.getPlace();
        date = club.getDate();
        leader = new UserResponse(club.getUser());
    }
}
