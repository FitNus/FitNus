package com.sparta.modulecommon.search.service;

import com.sparta.modulecommon.center.repository.CenterRepository;
import com.sparta.modulecommon.club.repository.ClubRepository;
import com.sparta.modulecommon.search.dto.response.SearchCenterResponse;
import com.sparta.modulecommon.search.dto.response.SearchClubResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ClubRepository clubRepository;
    private final CenterRepository centerRepository;

    public Page<SearchClubResponse> searchClubs(String clubName, String clubInfo, String place,
                                                int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());
        return clubRepository.search(clubName, clubInfo, place, pageable);
    }

    public Page<SearchCenterResponse> searchCenters(String centerName, String fitnessName, int page,
                                                    int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());
        return centerRepository.search(centerName, fitnessName, pageable);
    }
}
