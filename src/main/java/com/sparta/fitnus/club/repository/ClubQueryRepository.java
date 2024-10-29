package com.sparta.fitnus.club.repository;

import com.sparta.fitnus.search.dto.response.SearchClubResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubQueryRepository {

    Page<SearchClubResponse> search(
            String fitnessName,
            String clubName,
            String place,
            Pageable pageable
    );
}
