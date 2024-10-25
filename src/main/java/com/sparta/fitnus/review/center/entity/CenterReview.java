package com.sparta.fitnus.review.center.entity;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.review.center.dto.request.CenterReviewRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class CenterReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String writerNickname;

    private int centerScore;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    public CenterReview(CenterReviewRequest request, Center center) {
        this.userId = request.getUserId();
        this.writerNickname = request.getWriterNickName();
        this.centerScore = request.getCenterScore();
        this.comment = request.getComment();
        this.center = center;
    }

    public static CenterReview of(CenterReviewRequest request, Center center) {
        return new CenterReview(request, center);
    }

}
