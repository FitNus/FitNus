package com.sparta.fitnus.club.service;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.repository.ClubRepository;
import com.sparta.fitnus.common.exception.ClubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    /**
     * 모임 생성(추후에 멤버장 추가해야함)
     *
     * @param request : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse createClub(ClubRequest request) {
        Club newClub = Club.of(request);
        Club savedClub = clubRepository.save(newClub);

        return new ClubResponse(savedClub);
    }

    /**
     * 모임 수정
     *
     * @param request : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @param id      : 업데이트할 모임 ID
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse updateClub(ClubRequest request, long id) {
        Club club = isValidClub(id);

        club.update(request);

        return new ClubResponse(club);
    }

    /**
     * 모임 단건 조회
     *
     * @param id : 조회할 모임 ID
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    public ClubResponse getClub(long id) {
        return new ClubResponse(isValidClub(id));
    }

    /**
     * 모임 삭제
     *
     * @param id : 삭제할 모임 ID
     */
    @Transactional
    public void deleteClub(long id) {
        clubRepository.delete(isValidClub(id));
    }

    /**
     * 모임ID가 유효한지 확인
     *
     * @param id : 모임 id
     * @return Club : 모임 Entity 객체
     */
    private Club isValidClub(long id) {
        return clubRepository.findById(id).orElseThrow(ClubNotFoundException::new);
    }
}
