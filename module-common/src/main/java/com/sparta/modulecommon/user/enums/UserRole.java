package com.sparta.modulecommon.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER(Authority.USER),
    OWNER(Authority.OWNER),
    ADMIN(Authority.ADMIN);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다"));
    }

    public static class Authority {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
        public static final String OWNER = "OWNER";
    }
}
