package com.sparta.fitnus.member.repository;

import com.sparta.fitnus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
}
