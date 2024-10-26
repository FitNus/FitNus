package com.sparta.fitnus.user.dto.response;

import lombok.Getter;

@Getter
public class UserGetResponse {

    private final String nickname;
    private final String bio;
    private final String imageUrl;

    public UserGetResponse(String nickname, String bio, String imageUrl) {
        this.nickname = nickname;
        this.bio = bio;
        this.imageUrl = imageUrl;
    }
}
