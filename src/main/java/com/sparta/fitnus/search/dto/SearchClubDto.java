package com.sparta.fitnus.search.dto;

import com.sparta.fitnus.club.entity.Club;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchClubDto {

    private final Long id;
    private final String clubName;
    private final String clubInfo;
    private final String place;
    private final LocalDateTime date;

    public static SearchClubDto fromClub(Club club) {
        return new SearchClubDto(
                club.getId(),
                club.getClubName(),
                club.getClubInfo(),
                club.getPlace(),
                club.getDate()
        );
    }
}
