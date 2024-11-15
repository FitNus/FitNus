package com.sparta.service.applicant.repository;

import com.sparta.service.applicant.entity.MemberApplicant;
import com.sparta.service.club.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberApplicantsRepository extends JpaRepository<MemberApplicant, Long> {

    Optional<MemberApplicant> findByClubAndUserId(Club club, long userId);

    boolean existsByClubAndUserId(Club club, long userId);

    Page<MemberApplicant> findAllByClub(Club club, Pageable pageable);
}
