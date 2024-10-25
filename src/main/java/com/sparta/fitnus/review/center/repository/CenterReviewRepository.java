package com.sparta.fitnus.review.center.repository;

import com.sparta.fitnus.review.center.entity.CenterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CenterReviewRepository extends JpaRepository<CenterReview, Long> {
}


