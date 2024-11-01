package com.sparta.modulecommon.club.entity;

import com.sparta.modulecommon.applicant.entity.MemberApplicant;
import com.sparta.modulecommon.club.dto.request.ClubRequest;
import com.sparta.modulecommon.common.Timestamped;
import com.sparta.modulecommon.member.entity.Member;
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

    @Column(nullable = false)
    private Long leaderId;

    @Column(unique = true)
    private String clubName;

    private String clubInfo;

    private String place;

    private LocalDateTime date;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> memberList = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberApplicant> memberApplicantList = new ArrayList<>();

    private Club(ClubRequest request, long leaderId) {
        this.leaderId = leaderId;
        this.clubName = request.getClubName();
        this.clubInfo = request.getClubInfo();
        this.place = request.getPlace();
        this.date = request.getDate();
    }

    public static Club of(ClubRequest request, long leaderId) {
        return new Club(request, leaderId);
    }

    public void update(ClubRequest request) {
        this.clubName = request.getClubName();
        this.clubInfo = request.getClubInfo();
        this.place = request.getPlace();
        this.date = request.getDate();
    }
}

