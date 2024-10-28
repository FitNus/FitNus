package com.sparta.fitnus.member.entity;

import com.sparta.fitnus.applicant.entity.MemberApplicant;
import com.sparta.fitnus.club.entity.Club;
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

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    private Member(MemberApplicant memberApplicant) {
        memberRole = MemberRole.MEMBER;
        userId = memberApplicant.getUserId();
        club = memberApplicant.getClub();
    }

    private Member(Long userId, Club club) {
        memberRole = MemberRole.LEADER;
        this.userId = userId;
        this.club = club;
    }

    public static Member of(MemberApplicant memberApplicant) {
        return new Member(memberApplicant);
    }

    public static Member addLeaderOfClub(long userId, Club club) {
        return new Member(userId, club);
    }
}

