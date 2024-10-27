package com.sparta.fitnus.search.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.repository.CenterRepository;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.repository.ClubRepository;
import com.sparta.fitnus.search.dto.response.SearchCenterResponse;
import com.sparta.fitnus.search.dto.response.SearchClubResponse;
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

    public Page<SearchClubResponse> getClubs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());
        Page<Club> clubPage = clubRepository.findAll(pageable);
        return clubPage.map(SearchClubResponse::new);
    }

    public Page<SearchCenterResponse> getCenters(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Center> centerPage = centerRepository.findAll(pageable);
        return centerPage.map(SearchCenterResponse::new);
    }
}
