package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class UserNicknameResponse {

    private final String nickname;

    public UserNicknameResponse(User user) {
        this.nickname = user.getNickname();
    }
}
