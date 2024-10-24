package com.sparta.fitnus.member.controller;

import com.sparta.fitnus.common.apipayload.ApiResponse;
import com.sparta.fitnus.member.dto.request.MemberApplyRequest;
import com.sparta.fitnus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<String> applyMember(@RequestBody MemberApplyRequest request) {
        memberService.applyMember(request);
        return ApiResponse.createSuccess("모임 가입이 정상적으로 신청되었습니다.");
    }
}
