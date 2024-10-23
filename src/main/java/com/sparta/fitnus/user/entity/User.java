package com.sparta.fitnus.user.entity;


import com.sparta.fitnus.calender.entity.Calender;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.common.Timestamped;
import com.sparta.fitnus.review.profile.entity.ProfileReview;
import com.sparta.fitnus.user.dto.UserRequest;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.enums.UserStatus;
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

    @Column(unique = true)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    private String bio;
    private String image_url;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Club> clubList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "caleander_id")
    private Calender calender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ProfileReview> profileReviewList = new ArrayList<>();


    // 정팩매~~
    private User(UserRequest request){
        this.email = request.getEmail();
        this.userRole = UserRole.valueOf(request.getUserRole());
        this.password = request.getPassword();
        this.nickname = request.getNickname();
    }

    public static User of(UserRequest request) {
        return new User(request);
    }

}

