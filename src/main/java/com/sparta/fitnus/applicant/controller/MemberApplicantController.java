package com.sparta.fitnus.applicant.controller;

import com.sparta.fitnus.applicant.dto.response.MemberApplicantResponse;
import com.sparta.fitnus.applicant.service.MemberApplicantService;
import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Secured(UserRole.Authority.USER)
public class MemberApplicantController {

    private final MemberApplicantService memberApplicantService;

    @PostMapping("/v1/member-applicants/apply")
    public ApiResponse<String> createMemberApplicants(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRequest request
    ) {
        memberApplicantService.createMemberApplicants(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/member-applicants/accept")
    public ApiResponse<String> acceptMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberAcceptRequest request
    ) {
        memberApplicantService.acceptMember(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/member-applicants/reject")
    public ApiResponse<String> rejectMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRejectRequest request
    ) {
        memberApplicantService.rejectMember(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @GetMapping("/v1/member-applicants")
    public ApiResponse<Page<MemberApplicantResponse>> getMemberApplicantList(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody MemberRequest request
    ) {
        return ApiResponse.createSuccess(memberApplicantService.getMemberApplicantList(authUser, page, size, request));
    }
}
