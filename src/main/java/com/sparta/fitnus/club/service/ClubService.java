package com.sparta.fitnus.club.service;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.repository.ClubRepository;
import com.sparta.fitnus.common.exception.ClubNotFoundException;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;
    private final UserService userService;

    /**
     * 모임 생성
     *
     * @param request : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse createClub(AuthUser authUser, ClubRequest request) {
        User user = userService.getUser(authUser.getId());
        Club newClub = Club.of(request, user);
        Club savedClub = clubRepository.save(newClub);

        return new ClubResponse(savedClub);
    }

    /**
     * 모임 수정
     *
     * @param request : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @param clubId  : 업데이트할 모임 ID
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse updateClub(ClubRequest request, long clubId) {
        Club club = isValidClub(clubId);

        club.update(request);

        return new ClubResponse(club);
    }

    /**
     * 모임 단건 조회
     *
     * @param clubId : 조회할 모임 ID
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    public ClubResponse getClub(long clubId) {
        return new ClubResponse(isValidClub(clubId));
    }

    /**
     * 모임 삭제
     *
     * @param clubId : 삭제할 모임 ID
     */
    @Transactional
    public void deleteClub(long clubId) {
        clubRepository.delete(isValidClub(clubId));
    }

    /**
     * 모임ID가 유효한지 확인
     *
     * @param clubId : 모임 id
     * @return Club : 모임 Entity 객체
     */
    public Club isValidClub(long clubId) {
        return clubRepository.findById(clubId).orElseThrow(ClubNotFoundException::new);
    }
}
