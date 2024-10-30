package com.sparta.fitnus.member.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.member.dto.request.MemberDeportRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.member.dto.response.MemberResponse;
import com.sparta.fitnus.member.service.MemberService;
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
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/v1/members")
    public ApiResponse<Page<MemberResponse>> getMemberList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody MemberRequest request
    ) {
        return ApiResponse.createSuccess(memberService.getMemberList(page, size, request));
    }

    @DeleteMapping("/v1/members/withdraw")
    public ApiResponse<String> withdrawMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberRequest request
    ) {
        memberService.withdrawMember(authUser, request);
        return ApiResponse.createSuccess(null);
    }

    @DeleteMapping("/v1/members/deport")
    public ApiResponse<String> deportMember(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody MemberDeportRequest request
    ) {
        memberService.deportMember(authUser, request);
        return ApiResponse.createSuccess(null);
    }
}
