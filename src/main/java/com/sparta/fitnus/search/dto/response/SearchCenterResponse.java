package com.sparta.fitnus.search.dto.response;

import com.sparta.fitnus.center.entity.Center;
import lombok.Getter;

@Getter
//@AllArgsConstructor
public class SearchCenterResponse {

    private final Long id;
    private final String centerName;

    public SearchCenterResponse(Center center) {
        this.id = center.getId();
        this.centerName = center.getCenterName();
    }

//    public static SearchCenterResponse fromCenter(Center center) {
//        return new SearchCenterResponse(center.getId(), center.getCenterName());
//    }
}
