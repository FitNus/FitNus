package com.sparta.fitnus.user.dto.response;

import lombok.Getter;

@Getter
public class UserGetResponse {

    private final String nickname;
    private final String bio;

    public UserGetResponse(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }
}
