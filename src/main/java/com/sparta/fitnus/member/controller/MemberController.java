package com.sparta.fitnus.member.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberApplyRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.service.MemberService;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/v1/members/apply")
    public ApiResponse<String> applyMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberApplyRequest request
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
}
