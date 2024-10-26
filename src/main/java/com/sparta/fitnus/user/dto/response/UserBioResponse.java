package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class UserBioResponse {

    private final String bio;

    public UserBioResponse(User user) {
        this.bio = user.getBio();
    }
}
