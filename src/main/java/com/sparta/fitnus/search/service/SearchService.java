package com.sparta.fitnus.search.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.repository.ClubRepository;
import com.sparta.fitnus.search.dto.SearchClubDto;
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

    public Page<SearchClubDto> getClubs(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("modifiedAt").descending());
        Page<Club> clubPage = clubRepository.findAll(pageable);
        return clubPage.map(SearchClubDto::fromClub);
    }
}
