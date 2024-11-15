package com.sparta.service.club.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClubRequest {

    private String clubName;

    private String clubInfo;

    private String place;

    private LocalDateTime date;
}
