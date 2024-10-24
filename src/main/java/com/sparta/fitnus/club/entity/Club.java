package com.sparta.fitnus.club.entity;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.common.Timestamped;
import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Club extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String clubName;

    private String clubInfo;

    private String place;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberApplicant> memberApplicantList = new ArrayList<>();

    private Club(ClubRequest request, User user) {
        this.clubName = request.getClubName();
        this.clubInfo = request.getClubInfo();
        this.place = request.getPlace();
        this.date = request.getDate();
        this.user = user;
    }

    public static Club of(ClubRequest request, User user) {
        return new Club(request, user);
    }

    public void update(ClubRequest request) {
        this.clubName = request.getClubName();
        this.clubInfo = request.getClubInfo();
        this.place = request.getPlace();
        this.date = request.getDate();
    }
}

