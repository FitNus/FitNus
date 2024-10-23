package com.sparta.fitnus.club.entity;

import com.sparta.fitnus.common.Timestamped;
import com.sparta.fitnus.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Club extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long club_id;

    @Column(unique = true)
    private String club_name;

    private String club_info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



}

