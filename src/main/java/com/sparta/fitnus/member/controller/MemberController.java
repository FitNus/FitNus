package com.sparta.fitnus.member.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.member.applicant.dto.MemberApplicantResponse;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.service.MemberService;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/v1/members/apply")
    public ApiResponse<String> applyMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRequest request
    ) {
        return ApiResponse.createSuccess(memberService.applyMember(authUser, request));
    }

    @PostMapping("/v1/members/accept")
    public ApiResponse<MemberResponse> acceptMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberAcceptRequest request
    ) {
        return ApiResponse.createSuccess(memberService.acceptMember(authUser, request));
    }

    @PostMapping("/v1/members/reject")
    public ApiResponse<String> rejectMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRejectRequest request
    ) {
        return ApiResponse.createSuccess(memberService.rejectMember(authUser, request));
    }

    @GetMapping("/v1/members")
    public ApiResponse<Page<MemberResponse>> getMemberList(
            @RequestParam(defaultValue = "1") int page,
            @RequestBody MemberRequest request
    ) {
        return ApiResponse.createSuccess(memberService.getMemberList(page, request));
    }

    @GetMapping("/v1/members/applicants")
    public ApiResponse<Page<MemberApplicantResponse>> getMemberApplicantList(
            @RequestParam(defaultValue = "1") int page,
            @RequestBody MemberRequest request
    ) {
        return ApiResponse.createSuccess(memberService.getMemberApplicantList(page, request));
    }
}
