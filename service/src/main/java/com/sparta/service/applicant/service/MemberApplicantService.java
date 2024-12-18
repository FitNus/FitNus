package com.sparta.service.applicant.service;

import com.sparta.common.user.dto.AuthUser;
import com.sparta.common.user.dto.ProfileResponse;
import com.sparta.common.user.repository.UserRepository;
import com.sparta.service.applicant.dto.response.MemberApplicantResponse;
import com.sparta.service.applicant.entity.MemberApplicant;
import com.sparta.service.applicant.exception.AlreadyApplyException;
import com.sparta.service.applicant.exception.MemberApplicantNotFoundException;
import com.sparta.service.applicant.repository.MemberApplicantsRepository;
import com.sparta.service.club.entity.Club;
import com.sparta.service.club.service.ClubService;
import com.sparta.service.member.dto.request.MemberAcceptRequest;
import com.sparta.service.member.dto.request.MemberRejectRequest;
import com.sparta.service.member.dto.request.MemberRequest;
import com.sparta.service.member.entity.Member;
import com.sparta.service.member.service.MemberService;
import com.sparta.service.schedule.service.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberApplicantService {

    private final MemberApplicantsRepository memberApplicantsRepository;
    private final ClubService clubService;
    private final MemberService memberService;
    private final NotificationProducer notificationProducer;
    private final UserRepository userRepository;

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

        notificationProducer.sendClubNotification(
                club.getLeaderId(),
                authUser.getNickname() + " 님이 모임에 가입 신청을 했습니다.",
                club.getId()
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
                memberApplicant, new ProfileResponse(
                userRepository.findUserById(memberApplicant.getUserId()))));
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
