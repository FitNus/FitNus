package com.sparta.fitnus.member.applicant.dto;

import com.sparta.fitnus.member.applicant.entity.MemberApplicant;
import com.sparta.fitnus.user.dto.response.UserResponse;
import lombok.Getter;

@Getter
public class MemberApplicantResponse {

    private final Long memberApplicantId;
    private final UserResponse user;

    public MemberApplicantResponse(MemberApplicant memberApplicant, UserResponse user) {
        memberApplicantId = memberApplicant.getId();
        this.user = user;
    }
}
