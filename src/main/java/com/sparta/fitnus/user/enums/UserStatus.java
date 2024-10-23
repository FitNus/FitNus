package com.sparta.fitnus.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    ACTIVE(UserStatus.Authority.ACTIVE),
    BANNED(UserStatus.Authority.BANNED);

    private final String userRole;

    public static UserStatus of(String status) {
        return Arrays.stream(UserStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 Status"));
    }


    public static class Authority {
        public static final String ACTIVE = "ACTIVE";
        public static final String BANNED = "ADMIN";
    }
}

