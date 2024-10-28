package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileNicknameResponse {

    private final String nickname;

    public ProfileNicknameResponse(User user) {
        this.nickname = user.getNickname();
    }
}
