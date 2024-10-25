package com.sparta.fitnus.review.center.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.review.center.dto.request.CenterReviewRequest;
import com.sparta.fitnus.review.center.dto.response.CenterReviewResponse;
import com.sparta.fitnus.review.center.service.CenterReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CenterReviewController {
    private final CenterReviewService centerReviewService;

    // @PathVariable 로 centerId를 받는 대신, request에 직접 입력하게해서, request에서 centerId를 받는 방식임!
    @PostMapping("/v1/reviews/centers")
    public ApiResponse<CenterReviewResponse> addCenter(@RequestBody CenterReviewRequest request) {
        CenterReviewResponse response = centerReviewService.addCenterReview(request);

        return ApiResponse.createSuccess(response);
    }
}
