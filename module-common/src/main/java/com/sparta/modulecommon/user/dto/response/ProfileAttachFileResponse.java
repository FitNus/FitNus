package com.sparta.modulecommon.user.dto.response;

import com.sparta.modulecommon.user.entity.User;
import lombok.Getter;

@Getter
public class ProfileAttachFileResponse {

    private final String imageUrl;

    public ProfileAttachFileResponse(User user) {
        this.imageUrl = user.getImageUrl();
    }
}
