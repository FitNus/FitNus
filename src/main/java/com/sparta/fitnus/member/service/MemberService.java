package com.sparta.fitnus.member.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.member.dto.request.MemberDeportRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.exception.AlreadyMemberException;
import com.sparta.fitnus.member.exception.CanNotDeportLeaderException;
import com.sparta.fitnus.member.exception.MemberNotFound;
import com.sparta.fitnus.member.exception.NotLeaderException;
import com.sparta.fitnus.member.repository.MemberRepository;
import com.sparta.fitnus.ssenotification.entity.SseMessageName;
import com.sparta.fitnus.ssenotification.repository.NotificationRepository;
import com.sparta.fitnus.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.service.UserService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ClubService clubService;
    private final UserService userService;
    private final SseNotificationServiceImpl sseNotificationServiceImpl;
    private final NotificationRepository notificationRepository;

    /**
     * 멤버 가입신청
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입할 모임 ID를 담고 있는 DTO
     * @return String : API 성공 응답메세지
     */
    @Transactional
    public String applyMember(AuthUser authUser, MemberRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        MemberApplicant memberApplicant = MemberApplicant.of(authUser.getId(), club);
        memberApplicantsRepository.save(memberApplicant);

        // 모임 리더에게 가입 신청 알림 엔티티 저장
        User leader = club.getUser();

        sseNotificationServiceImpl.broadcast(SseMessageName.MESSAGE,
            leader.getId(),
            "가입 신청",
            authUser.getNickname() + " 님이 모임에 가입 신청을 했습니다.",
            LocalDateTime.now()
        );  // 모임 리더에게 알림 전송

        return "모임 가입이 정상적으로 신청되었습니다.";
    }

    /**
     * @param page    : 기본값이 1인 page 번호
     * @param size    : 기본값이 1인 size 크기
     * @param request : 조회할 모임 ID를 담고 있는 DTO
     * @return Page<MemberResponse> : 모임의 멤버 목록을 페이지네이션 한 객체
     */
    public Page<MemberResponse> getMemberList(int page, int size, MemberRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Club club = clubService.isValidClub(request.getClubId());

        Page<Member> memberPage = memberRepository.findAllByClub(club, pageable);

        return memberPage.map(member -> new MemberResponse(
                member, new ProfileResponse(userService.getUser(member.getUserId()))));
    }

    /**
     * 멤버 탈퇴
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 조회할 모임 ID를 담고 있는 DTO
     */
    @Transactional
    public void withdrawMember(AuthUser authUser, MemberRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        isValidMemberOfClub(club, authUser.getId());

        memberRepository.deleteByClubAndUserId(club, authUser.getId());
    }

    /**
     * 멤버 추방
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 추방할 userId, 조회할 모임 ID를 담고 있는 DTO
     */
    @Transactional
    public void deportMember(AuthUser authUser, MemberDeportRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        isLeaderOfClub(club, authUser.getId());

        isValidMemberOfClub(club, authUser.getId());
        if (request.getUserId().equals(club.getLeaderId())) {
            throw new CanNotDeportLeaderException();
        }

        memberRepository.deleteByClubAndUserId(club, request.getUserId());
    }

    /**
     * 모임의 리더인지 확인
     *
     * @param club   : 확인할 모임 Entity 객체
     * @param userId : 리더인지 확인할 사용자의 ID
     */
    public void isLeaderOfClub(Club club, long userId) {
        if (!club.getLeaderId().equals(userId)) {
            throw new NotLeaderException();
        }
    }

    /**
     * 이미 멤버인지 확인
     *
     * @param club   : 확인할 모임 Entity 객체
     * @param userId : 멤버인지 확인할 사용자 ID
     */
    public void isAlreadyMember(Club club, long userId) {
        if (memberRepository.existsByClubAndUserId(club, userId)) {
            throw new AlreadyMemberException();
        }
    }

    /**
     * 멤버가 유효한지 확인
     *
     * @param club   : 확인할 모임 Entity 객체
     * @param userId : 확인할 사용자 ID
     */
    private void isValidMemberOfClub(Club club, long userId) {
        if (!memberRepository.existsByClubAndUserId(club, userId)) {
            throw new MemberNotFound();
        }
    }
}
