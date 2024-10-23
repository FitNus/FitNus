package com.sparta.fitnus.club.entity;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.common.Timestamped;
import com.sparta.fitnus.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private Club(ClubRequest request) {
        this.clubName = request.getClubName();
        this.clubInfo = request.getClubInfo();
        this.place = request.getPlace();
        this.date = request.getDate();
    }

    public static Club of(ClubRequest request) {
        return new Club(request);
    }
}

