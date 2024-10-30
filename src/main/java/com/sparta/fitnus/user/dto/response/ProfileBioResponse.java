package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileBioResponse {

    private final String bio;

    public ProfileBioResponse(User user) {
        this.bio = user.getBio();
    }
}
