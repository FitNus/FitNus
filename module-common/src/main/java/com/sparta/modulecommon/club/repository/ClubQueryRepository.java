package com.sparta.modulecommon.club.repository;

import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubQueryRepository {

    Page<SearchClubResponse> search(
            String clubName,
            String clubInfo,
            String place,
            Pageable pageable
    );
}
