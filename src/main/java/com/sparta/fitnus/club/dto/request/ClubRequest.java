package com.sparta.fitnus.club.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClubRequest {

    private String clubName;

    private String clubInfo;

    private String place;

    private LocalDateTime date;
}
