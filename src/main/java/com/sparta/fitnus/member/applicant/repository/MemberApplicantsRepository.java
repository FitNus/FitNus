package com.sparta.fitnus.member.applicant.repository;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberApplicantsRepository extends JpaRepository<MemberApplicant, Long> {
    MemberApplicant findByClubAndUserId(Club club, long userId);
}
