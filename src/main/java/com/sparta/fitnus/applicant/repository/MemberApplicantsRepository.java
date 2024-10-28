package com.sparta.fitnus.applicant.repository;

import com.sparta.fitnus.applicant.entity.MemberApplicant;
import com.sparta.fitnus.club.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberApplicantsRepository extends JpaRepository<MemberApplicant, Long> {

    MemberApplicant findByClubAndUserId(Club club, long userId);

    Page<MemberApplicant> findAllByClub(Pageable pageable, Club club);
}
