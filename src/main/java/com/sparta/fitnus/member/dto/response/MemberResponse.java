package com.sparta.fitnus.member.dto.response;

import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.enums.MemberRole;
import com.sparta.fitnus.user.dto.response.ProfileResponse;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long memberId;
    private final MemberRole memberRole;
    private final ProfileResponse userProfile;

    public MemberResponse(Member member, ProfileResponse userProfile) {
        memberId = member.getId();
        memberRole = member.getMemberRole();
        this.userProfile = userProfile;
    }
}
