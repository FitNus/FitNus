package com.sparta.fitnus.member.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.common.exception.NotLeaderException;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.applicant.repository.MemberApplicantsRepository;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberApplyRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.repository.MemberRepository;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberApplicantsRepository memberApplicantsRepository;
    private final ClubService clubService;
    private final UserService userService;

    public void applyMember(AuthUser authUser, MemberApplyRequest request) {
        Club club = clubService.isValidClub(request.getClubId());

        MemberApplicant memberApplicant = MemberApplicant.of(authUser.getId(), club);
        memberApplicantsRepository.save(memberApplicant);
    }

    public MemberResponse acceptMember(AuthUser authUser, MemberAcceptRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        if (!club.getUser().getId().equals(authUser.getId())) {
            throw new NotLeaderException();
        }

        MemberApplicant memberApplicant = memberApplicantsRepository.findByClubAndUserId(club, request.getUserId());
        memberApplicantsRepository.delete(memberApplicant);

        Member newMember = Member.of(memberApplicant);
        Member savedMember = memberRepository.save(newMember);

        User user = userService.getUser(savedMember.getUserId());

        return new MemberResponse(savedMember, new UserResponse(user));
    }
}
