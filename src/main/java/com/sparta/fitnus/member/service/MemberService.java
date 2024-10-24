package com.sparta.fitnus.member.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.exception.NotLeaderException;
import com.sparta.fitnus.member.applicant.dto.MemberApplicantResponse;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.applicant.repository.MemberApplicantsRepository;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.repository.MemberRepository;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.UserService;
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
    public String applyMember(AuthUser authUser, MemberRequest request) {
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

    /**
     * 멤버 목록 조회
     *
     * @param page    : 기본값이 1인 page 번호
     * @param request : 조회할 모임 ID를 담고 있는 DTO
     * @return Page<MemberResponse> : 모임의 멤버 목록을 페이지네이션 한 객체
     */
    public Page<MemberResponse> getMemberList(int page, MemberRequest request) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        Club club = clubService.isValidClub(request.getClubId());

        Page<Member> memberPage = memberRepository.findAllByClub(pageable, club);

        return memberPage.map(member -> new MemberResponse(
                member, new UserResponse(userService.getUser(member.getUserId()))));
    }

    /**
     * 멤버 신청목록 조회
     *
     * @param page    : 기본값이 1인 page 번호
     * @param request : 조회할 모임 ID를 담고 있는 DTO
     * @return Page<MemberApplicantResponse> : 모임의 멤버 신청목록을 페이지네이션 한 객체
     */
    public Page<MemberApplicantResponse> getMemberApplicantList(int page, MemberRequest request) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        Club club = clubService.isValidClub(request.getClubId());

        Page<MemberApplicant> memberApplicantPage = memberApplicantsRepository.findAllByClub(pageable, club);

        return memberApplicantPage.map(memberApplicant -> new MemberApplicantResponse(
                memberApplicant, new UserResponse(userService.getUser(memberApplicant.getUserId()))));
    }

    /**
     * 멤버 탈퇴
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param request  : 조회할 모임 ID를 담고 있는 DTO
     * @return String : API 성공 응답메세지
     */
    @Transactional
    public String withdrawMember(AuthUser authUser, MemberRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        memberRepository.deleteByClubAndUserId(club, authUser.getId());

        return "모임에서 정상적으로 탈퇴되었습니다.";
    }

    /**
     * 모임의 리더인지 확인
     *
     * @param club   : 확인할 모임 Entity 객체
     * @param userId : 리더인지 확인할 사용자의 ID
     */
    private void isLeaderOfClub(Club club, long userId) {
        if (!club.getUser().getId().equals(userId)) {
            throw new NotLeaderException();
        }
    }
}
