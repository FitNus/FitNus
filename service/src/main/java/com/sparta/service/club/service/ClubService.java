package com.sparta.service.club.service;

import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.club.dto.request.ClubRequest;
import com.sparta.service.club.dto.response.ClubResponse;
import com.sparta.service.club.entity.Club;
import com.sparta.service.club.exception.AlreadyExistsClubNameException;
import com.sparta.service.club.exception.ClubNotFoundException;
import com.sparta.service.club.repository.ClubRepository;
import com.sparta.service.member.entity.Member;
import com.sparta.service.member.exception.NotLeaderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;

    /**
     * 모임 생성
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse createClub(AuthUser authUser, ClubRequest request) {
        isExistsByClubName(request.getClubName());

        Club newClub = Club.of(request, authUser.getId());
        Club savedClub = clubRepository.save(newClub);

        savedClub.getMemberList().add(Member.addLeaderOfClub(authUser.getId(), savedClub));

        return new ClubResponse(savedClub);
    }

    /**
     * 모임 수정
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : clubName, clubInfo, place, date가 담긴 DTO 객체
     * @param clubId   : 업데이트할 모임 ID
     * @return ClubResponse : id, clubName, clubInfo, place, date가 담긴 DTO 객체
     */
    @Transactional
    public ClubResponse updateClub(AuthUser authUser, ClubRequest request, long clubId) {
        isExistsByClubName(request.getClubName());

        Club club = isValidClub(clubId);
        isLeaderOfClub(authUser.getId(), club);

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
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param clubId   : 삭제하려는 모임 ID
     */
    @Transactional
    public void deleteClub(AuthUser authUser, long clubId) {
        Club club = isValidClub(clubId);
        isLeaderOfClub(authUser.getId(), club);

        clubRepository.delete(club);
    }

    /**
     * 모임 이름이 중복되는지 확인
     *
     * @param clubName : 확인할 모임의 이름
     */
    private void isExistsByClubName(String clubName) {
        if (clubRepository.existsByClubName(clubName)) {
            throw new AlreadyExistsClubNameException();
        }
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

    /**
     * 업데이트나 삭제하려는 사용자가 모임의 리더인지 확인
     *
     * @param userId : 업데이트나 삭제하려는 사용자 ID
     * @param club   : 업데이트나 삭제하려는 모임 Entity 객체
     */
    private void isLeaderOfClub(long userId, Club club) {
        if (userId != club.getLeaderId()) {
            throw new NotLeaderException();
        }
    }
}
