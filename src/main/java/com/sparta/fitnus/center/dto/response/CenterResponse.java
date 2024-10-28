package com.sparta.fitnus.center.dto.response;

import com.sparta.fitnus.center.entity.Center;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CenterResponse {

    private final Long id;
    private final String center_name;
    private final Integer open_time;
    private final Integer close_time;


    public CenterResponse(Center center) {
        this.id = center.getId();
        this.center_name = center.getCenterName();
        this.open_time = center.getOpenTime();
        this.close_time = center.getCloseTime();
    }

}
