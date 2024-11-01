package com.sparta.modulecommon.applicant.service;

import com.sparta.modulecommon.applicant.dto.response.MemberApplicantResponse;
import com.sparta.modulecommon.applicant.entity.MemberApplicant;
import com.sparta.modulecommon.applicant.exception.AlreadyApplyException;
import com.sparta.modulecommon.applicant.exception.MemberApplicantNotFoundException;
import com.sparta.modulecommon.applicant.repository.MemberApplicantsRepository;
import com.sparta.modulecommon.club.entity.Club;
import com.sparta.modulecommon.club.service.ClubService;
import com.sparta.modulecommon.member.dto.request.MemberAcceptRequest;
import com.sparta.modulecommon.member.dto.request.MemberRejectRequest;
import com.sparta.modulecommon.member.dto.request.MemberRequest;
import com.sparta.modulecommon.member.entity.Member;
import com.sparta.modulecommon.member.service.MemberService;
import com.sparta.modulecommon.ssenotification.entity.SseMessageName;
import com.sparta.modulecommon.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.modulecommon.user.dto.response.ProfileResponse;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberApplicantService {

    private final MemberApplicantsRepository memberApplicantsRepository;
    private final ClubService clubService;
    private final MemberService memberService;
    private final UserService userService;
    private final SseNotificationServiceImpl sseNotificationServiceImpl;

    /**
     * 멤버 가입신청
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입할 모임 ID를 담고 있는 DTO
     */
    @Transactional
    public void createMemberApplicant(AuthUser authUser, MemberRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        memberService.isAlreadyMember(club, authUser.getId());
        if (memberApplicantsRepository.existsByClubAndUserId(club, authUser.getId())) {
            throw new AlreadyApplyException();
        }

        MemberApplicant memberApplicant = MemberApplicant.of(authUser.getId(), club);
        memberApplicantsRepository.save(memberApplicant);

        sseNotificationServiceImpl.broadcast(SseMessageName.MESSAGE,
            club.getLeaderId(),
            "가입 신청",
            authUser.getNickname() + " 님이 모임에 가입 신청을 했습니다.",
            LocalDateTime.now()
        );  // 모임 리더에게 알림 전송
    }

    /**
     * 멤버 가입신청 수락
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
     */
    @Transactional
    public void acceptMemberApplicant(AuthUser authUser, MemberAcceptRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        memberService.isLeaderOfClub(club, authUser.getId());

        MemberApplicant memberApplicant = isValidApplicant(club, request.getUserId());

        Member member = Member.of(memberApplicant);
        club.getMemberList().add(member);

        memberApplicantsRepository.delete(memberApplicant);
    }

    /**
     * 멤버 가입신청 거절
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
     */
    @Transactional
    public void rejectMemberApplicant(AuthUser authUser, MemberRejectRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        memberService.isLeaderOfClub(club, authUser.getId());

        MemberApplicant memberApplicant = isValidApplicant(club, request.getUserId());
        memberApplicantsRepository.delete(memberApplicant);
    }

    /**
     * 멤버 신청목록 조회
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param page     : 기본값이 1인 page 번호
     * @param size     : 기본값이 1인 size 크기
     * @param request  : 조회할 모임 ID를 담고 있는 DTO
     * @return Page<MemberApplicantResponse> : 모임의 멤버 신청목록을 페이지네이션 한 객체
     */
    public Page<MemberApplicantResponse> getMemberApplicantList(AuthUser authUser, int page, int size, MemberRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Club club = clubService.isValidClub(request.getClubId());
        memberService.isLeaderOfClub(club, authUser.getId());

        Page<MemberApplicant> memberApplicantPage = memberApplicantsRepository.findAllByClub(club, pageable);

        return memberApplicantPage.map(memberApplicant -> new MemberApplicantResponse(
                memberApplicant, new ProfileResponse(userService.getUser(memberApplicant.getUserId()))));
    }

    /**
     * 유효한 신청자인지 확인
     *
     * @param club   : 확인할 모임 Entity 객체
     * @param userId : 확인할 사용자 ID
     * @return MemberApplicant : 신청자 Entity 객체
     */
    private MemberApplicant isValidApplicant(Club club, long userId) {
        return memberApplicantsRepository.findByClubAndUserId(club, userId)
                .orElseThrow(MemberApplicantNotFoundException::new);
    }
}
