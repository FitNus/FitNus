package com.sparta.fitnus.review.center.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.review.center.dto.request.CenterReviewRequest;
import com.sparta.fitnus.review.center.dto.response.CenterReviewResponse;
import com.sparta.fitnus.review.center.entity.CenterReview;
import com.sparta.fitnus.review.center.repository.CenterReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterReviewService {
    private final CenterReviewRepository centerReviewRepository;
    private final CenterService centerService;

    /***
     * CRUD-POST : saveCenterReview()의 기능입니다.
     * @param request
     * @return
     */
    @Transactional
    public CenterReviewResponse addCenterReview(CenterReviewRequest request) {
        Center center = centerService.getCenterId(request.getCenterId());
        CenterReview review = new CenterReview(request, center);
        CenterReview savedReview = centerReviewRepository.save(review);
        return new CenterReviewResponse(savedReview);
    }
}
