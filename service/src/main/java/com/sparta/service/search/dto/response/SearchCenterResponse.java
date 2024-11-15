package com.sparta.service.search.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchCenterResponse {

    private final Long centerId;
    private final String centerName;
    private final String address;
    private final List<String> fitnessName;
}
