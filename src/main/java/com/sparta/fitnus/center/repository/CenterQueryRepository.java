package com.sparta.fitnus.center.repository;

import com.sparta.fitnus.search.dto.response.SearchCenterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CenterQueryRepository {

    Page<SearchCenterResponse> search(
            String centerName,
            String fitnessName,
            Pageable pageable
    );
}
