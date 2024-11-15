package com.sparta.service.member.repository;

import com.sparta.service.club.entity.Club;
import com.sparta.service.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAllByClub(Club club, Pageable pageable);

    void deleteByClubAndUserId(Club club, long userId);

    boolean existsByClubAndUserId(Club club, long userId);
}
