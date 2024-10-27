package com.sparta.fitnus.search.dto;

import com.sparta.fitnus.center.entity.Center;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchCenter {

    private final Long id;
    private final String centerName;

    public static SearchCenter fromCenter(Center center) {
        return new SearchCenter(center.getId(), center.getCenterName());
    }
}
