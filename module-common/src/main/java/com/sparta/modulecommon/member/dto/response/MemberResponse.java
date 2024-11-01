package com.sparta.modulecommon.member.dto.response;

import com.sparta.modulecommon.member.entity.Member;
import com.sparta.modulecommon.member.enums.MemberRole;
import com.sparta.modulecommon.user.dto.response.ProfileResponse;
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
