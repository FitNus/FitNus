package com.sparta.fitnus.user.dto.response;

import com.sparta.fitnus.user.entity.User;
import lombok.Getter;

@Getter
public class UserAttachFileResponse {

    private final String imageUrl;

    public UserAttachFileResponse(User user) {
        this.imageUrl = user.getImageUrl();
    }
}
