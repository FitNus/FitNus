//package com.sparta.fitnus.applicant.service;
//
//import com.sparta.fitnus.applicant.entity.MemberApplicant;
//import com.sparta.fitnus.applicant.repository.MemberApplicantsRepository;
//import com.sparta.fitnus.club.entity.Club;
//import com.sparta.fitnus.club.service.ClubService;
//import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
//import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
//import com.sparta.fitnus.member.dto.request.MemberRequest;
//import com.sparta.fitnus.member.dto.response.MemberResponse;
//import com.sparta.fitnus.member.entity.Member;
//import com.sparta.fitnus.ssenotification.dto.EventPayload;
//import com.sparta.fitnus.user.dto.response.UserResponse;
//import com.sparta.fitnus.user.entity.AuthUser;
//import com.sparta.fitnus.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class MemberApplicantService {
//
//    private final MemberApplicantsRepository memberApplicantsRepository;
//    private final ClubService clubService;
//
//    /**
//     * 멤버 가입신청
//     *
//     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
//     * @param request  : 가입할 모임 ID를 담고 있는 DTO
//     */
//    @Transactional
//    public void applyMember(AuthUser authUser, MemberRequest request) {
//        Club club = clubService.isValidClub(request.getClubId());
//
//        memberApplicantsRepository.findByClubAndUserId(club, authUser.getId());
//        MemberApplicant memberApplicant = MemberApplicant.of(authUser.getId(), club);
//        memberApplicantsRepository.save(memberApplicant);
//
//        // 모임 리더에게 가입 신청 알림 전송
//        EventPayload eventPayload = new EventPayload(
//                "가입 신청",
//                authUser.getNickname() + " 님이 모임에 가입 신청을 했습니다.",
//                LocalDate.now());
//
//        sseNotificationServiceImpl.broadcast(club.getLeaderId(), eventPayload);  // 모임 리더에게 알림 전송
//    }
//
//    /**
//     * 멤버 가입신청 수락
//     *
//     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
//     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
//     * @return MemberResponse : 멤버 ID와 유저 프로필, 멤버 역할을 담고 있는 DTO
//     */
//    @Transactional
//    public MemberResponse acceptMember(AuthUser authUser, MemberAcceptRequest request) {
//        Club club = clubService.isValidClub(request.getClubId());
//        isLeaderOfClub(club, authUser.getId());
//
//        MemberApplicant memberApplicant = memberApplicantsRepository.findByClubAndUserId(club, request.getUserId());
//        memberApplicantsRepository.delete(memberApplicant);
//
//        Member newMember = Member.of(memberApplicant);
//        Member savedMember = memberRepository.save(newMember);
//
//        User user = userService.getUser(savedMember.getUserId());
//
//        return new MemberResponse(savedMember, new UserResponse(user));
//    }
//
//    /**
//     * 멤버 가입신청 거절
//     *
//     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
//     * @param request  : 가입신청한 사용자 ID, 가입할 모임 ID를 담고 있는 DTO
//     */
//    @Transactional
//    public void rejectMember(AuthUser authUser, MemberRejectRequest request) {
//        Club club = clubService.isValidClub(request.getClubId());
//
//        isLeaderOfClub(club, authUser.getId());
//
//        MemberApplicant memberApplicant = memberApplicantsRepository.findByClubAndUserId(club, request.getUserId());
//        memberApplicantsRepository.delete(memberApplicant);
//    }
//}