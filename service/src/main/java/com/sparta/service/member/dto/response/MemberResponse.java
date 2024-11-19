package com.sparta.service.member.dto.response;

import com.sparta.common.user.dto.ProfileResponse;
import com.sparta.service.member.entity.Member;
import com.sparta.service.member.enums.MemberRole;
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
