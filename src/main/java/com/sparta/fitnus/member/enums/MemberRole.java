package com.sparta.fitnus.member.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    LEADER(Authority.LEADER),
    MEMBER(Authority.MEMBER);

    private final String memberRole;

    public static MemberRole of(String role) {
        return Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다"));
    }

    public static class Authority {
        public static final String LEADER = "LEADER";
        public static final String MEMBER = "MEMBER";
    }
}
