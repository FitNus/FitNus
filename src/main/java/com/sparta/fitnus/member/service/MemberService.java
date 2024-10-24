package com.sparta.fitnus.member.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.exception.NotLeaderException;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.applicant.repository.MemberApplicantsRepository;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberApplyRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.repository.MemberRepository;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberApplicantsRepository memberApplicantsRepository;
    private final ClubService clubService;
    private final UserService userService;

    /**
     * 멤버 가입신청
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입할 모임 ID를 담고 있는 DTO
     * @return String : API 성공 응답메세지
     */
    @Transactional
    public String applyMember(AuthUser authUser, MemberApplyRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        MemberApplicant memberApplicant = MemberApplicant.of(authUser.getId(), club);
        memberApplicantsRepository.save(memberApplicant);

        return "모임 가입이 정상적으로 신청되었습니다.";
    }

    /**
     * 멤버 가입신청 수락
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
     * @return MemberResponse : 멤버 ID와 유저 프로필, 멤버 역할을 담고 있는 DTO
     */
    @Transactional
    public MemberResponse acceptMember(AuthUser authUser, MemberAcceptRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        isLeaderOfClub(club, authUser.getId());

        MemberApplicant memberApplicant = memberApplicantsRepository.findByClubAndUserId(club, request.getUserId());
        memberApplicantsRepository.delete(memberApplicant);

        Member newMember = Member.of(memberApplicant);
        Member savedMember = memberRepository.save(newMember);

        User user = userService.getUser(savedMember.getUserId());

        return new MemberResponse(savedMember, new UserResponse(user));
    }

    /**
     * 멤버 가입신청 거절
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
     * @return String : API 성공 응답메세지
     */
    @Transactional
    public String rejectMember(AuthUser authUser, MemberRejectRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        isLeaderOfClub(club, authUser.getId());

        MemberApplicant memberApplicant = memberApplicantsRepository.findByClubAndUserId(club, request.getUserId());
        memberApplicantsRepository.delete(memberApplicant);

        return "모임 가입신청이 정상적으로 거절되었습니다.";
    }

    private void isLeaderOfClub(Club club, long userId) {
        if (!club.getUser().getId().equals(userId)) {
            throw new NotLeaderException();
        }
    }
}
