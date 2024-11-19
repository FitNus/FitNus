package com.sparta.user.user.dto.response;

import com.sparta.common.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileUpdateResponse {

    private final String bio;
    private final String nickname;

    public ProfileUpdateResponse(User user) {
        this.bio = user.getBio();
        this.nickname = user.getNickname();
    }
}
