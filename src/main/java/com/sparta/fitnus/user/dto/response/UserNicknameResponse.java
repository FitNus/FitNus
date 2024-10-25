package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNicknameResponse {

    private final String nickname;

    public static UserNicknameResponse entityToDto(User user) {
        return new UserNicknameResponse(user.getNickname());
    }
}
