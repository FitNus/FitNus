package com.sparta.fitnus.applicant.entity;

import com.sparta.fitnus.club.entity.Club;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class MemberApplicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    private MemberApplicant(Long userId, Club club) {
        this.userId = userId;
        this.club = club;
    }

    public static MemberApplicant of(Long userId, Club club) {
        return new MemberApplicant(userId, club);
    }
}