package com.sparta.service.member.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberRoleTest {

    @Test
    public void of_ShouldReturnLeader_WhenValidRole() {
        // given
        String role = "LEADER";

        // when
        MemberRole memberRole = MemberRole.of(role);

        // then
        assertEquals(MemberRole.LEADER, memberRole);
    }

    @Test
    public void of_ShouldReturnMember_WhenValidRole() {
        // given
        String role = "MEMBER";

        // when
        MemberRole memberRole = MemberRole.of(role);

        // then
        assertEquals(MemberRole.MEMBER, memberRole);
    }

    @Test
    public void of_ShouldThrowException_WhenInvalidRole() {
        // given
        String role = "INVALID_ROLE";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            MemberRole.of(role);
        });

        assertEquals("존재하지 않는 권한입니다", exception.getMessage());
    }
}