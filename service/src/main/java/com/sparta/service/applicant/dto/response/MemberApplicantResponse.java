package com.sparta.service.applicant.dto.response;

import com.sparta.common.user.dto.ProfileResponse;
import com.sparta.service.applicant.entity.MemberApplicant;
import lombok.Getter;

@Getter
public class MemberApplicantResponse {

    private final Long memberApplicantId;
    private final ProfileResponse userProfile;

    public MemberApplicantResponse(MemberApplicant memberApplicant, ProfileResponse userProfile) {
        memberApplicantId = memberApplicant.getId();
        this.userProfile = userProfile;
    }
}
