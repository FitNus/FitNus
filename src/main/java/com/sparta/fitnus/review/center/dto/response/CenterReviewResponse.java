package com.sparta.fitnus.review.center.dto.response;

import com.sparta.fitnus.review.center.entity.CenterReview;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CenterReviewResponse {
    private final int centerScore;
    private final String comment;

    public CenterReviewResponse(CenterReview centerReview) {
        this.centerScore = centerReview.getCenterScore();
        this.comment = centerReview.getComment();
    }
}
