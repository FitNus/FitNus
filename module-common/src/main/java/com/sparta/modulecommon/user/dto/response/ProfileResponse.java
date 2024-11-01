package com.sparta.modulecommon.user.dto.response;

import com.sparta.modulecommon.user.entity.User;
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
