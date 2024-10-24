package com.sparta.fitnus.member.dto.response;

import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.enums.MemberRole;
import com.sparta.fitnus.user.dto.response.UserResponse;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long memberId;
    private final UserResponse user;
    private final MemberRole memberRole;

    public MemberResponse(Member member, UserResponse user) {
        memberId = member.getId();
        this.user = user;
        memberRole = member.getMemberRole();
    }
}
