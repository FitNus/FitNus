package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBioResponse {

    private final String bio;

    public static UserBioResponse entityToDto(User user) {
        return new UserBioResponse(user.getBio());
    }
}
