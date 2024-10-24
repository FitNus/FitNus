package com.sparta.fitnus.member.service;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.applicant.repository.MemberApplicantsRepository;
import com.sparta.fitnus.member.dto.request.MemberApplyRequest;
import com.sparta.fitnus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberApplicantsRepository memberApplicantsRepository;
    private final ClubService clubService;

    public void applyMember(MemberApplyRequest request) {
        Club club = clubService.isValidClub(request.getClubId());
        
        MemberApplicant memberApplicant = MemberApplicant.of(2L, club);
        memberApplicantsRepository.save(memberApplicant);
    }
}
