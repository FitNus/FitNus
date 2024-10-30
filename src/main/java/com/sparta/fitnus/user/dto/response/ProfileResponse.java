package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileResponse {

    private final String nickname;
    private final String bio;
    private final String imageUrl;

    public ProfileResponse(User user) {
        this.nickname = user.getNickname();
        this.bio = user.getBio();
        this.imageUrl = user.getImageUrl();
    }
}
