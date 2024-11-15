package com.sparta.modulecommon.applicant.controller;

import com.sparta.modulecommon.applicant.dto.response.MemberApplicantResponse;
import com.sparta.modulecommon.applicant.service.MemberApplicantService;
import com.sparta.modulecommon.common.apipayload.ApiResponse;
import com.sparta.modulecommon.member.dto.request.MemberAcceptRequest;
import com.sparta.modulecommon.member.dto.request.MemberRejectRequest;
import com.sparta.modulecommon.member.dto.request.MemberRequest;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.enums.UserRole;
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

    @PostMapping("/v1/member-applicants")
    public ApiResponse<String> createMemberApplicant(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRequest request
    ) {
        memberApplicantService.createMemberApplicant(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/member-applicants/accept")
    public ApiResponse<String> acceptMemberApplicant(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberAcceptRequest request
    ) {
        memberApplicantService.acceptMemberApplicant(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @PostMapping("/v1/member-applicants/reject")
    public ApiResponse<String> rejectMemberApplicant(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRejectRequest request
    ) {
        memberApplicantService.rejectMemberApplicant(authUser, request);
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