package com.sparta.fitnus.user.entity;


import com.sparta.common.Timestamped;
import com.sparta.fitnus.calender.entity.Calender;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.review.profile.entity.ProfileReview;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.enums.UserStatus;
import com.sparta.fitnus.user.request.UserRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(unique = true, length = 50, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    private String bio;
    private String image_url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Club> clubList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "caleander_id")
    private Calender calender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ProfileReview> profileReviewList = new ArrayList<>();


    // 정팩매~~
    private User(UserRequest request, UserRole role) {
        this.email = request.getEmail();
        this.userRole = role;
        this.password = request.getPassword();
        this.nickname = request.getNickname();
        this.status = UserStatus.ACTIVE;
    }

    public static User of(UserRequest request, UserRole role) {
        return new User(request, role);
    }

}

