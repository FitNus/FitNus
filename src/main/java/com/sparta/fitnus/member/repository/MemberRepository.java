package com.sparta.fitnus.member.repository;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAllByClub(Pageable pageable, Club club);

    void deleteByClubAndUserId(Club club, long userId);
}
