package com.sparta.modulecommon.member.entity;

import com.sparta.modulecommon.applicant.entity.MemberApplicant;
import com.sparta.modulecommon.club.entity.Club;
import com.sparta.modulecommon.member.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column(name = "user_id", nullable = false)
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

