package com.sparta.service.club.entity;

import com.sparta.service.applicant.entity.MemberApplicant;
import com.sparta.service.club.dto.request.ClubRequest;
import com.sparta.common.Timestamped;
import com.sparta.service.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "club")
public class Club extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    @Column(name = "club_name", unique = true)
    private String clubName;

    @Column(name = "club_info")
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

