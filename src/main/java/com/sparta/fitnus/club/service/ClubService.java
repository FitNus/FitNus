package com.sparta.fitnus.club.service;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubResponse createClub(ClubRequest request) {
        Club newClub = Club.of(request);
        Club savedClub = clubRepository.save(newClub);

//        return new ClubResponse(savedClub);
        return null;
    }
}
