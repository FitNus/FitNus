package com.sparta.fitnus.review.center.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CenterReviewRequest {
    private Long userId;

    private Long centerId;

    private String writerNickName;

    private int centerScore;

    private String comment;
}
