package com.sparta.fitnus.member.entity;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MemberRole memberRole;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    private Member(MemberApplicant memberApplicant) {
        this.memberRole = MemberRole.MEMBER;
        this.userId = memberApplicant.getUserId();
        this.club = memberApplicant.getClub();
    }

    public static Member of(MemberApplicant memberApplicant) {
        return new Member(memberApplicant);
    }
}

