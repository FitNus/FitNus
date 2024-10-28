//package com.sparta.fitnus.applicant.controller;
//
//import com.sparta.fitnus.applicant.service.MemberApplicantService;
//import com.sparta.fitnus.common.apipayload.ApiResponse;
//import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
//import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
//import com.sparta.fitnus.member.dto.request.MemberRequest;
//import com.sparta.fitnus.member.dto.response.MemberResponse;
//import com.sparta.fitnus.user.entity.AuthUser;
//import com.sparta.fitnus.user.enums.UserRole;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//@Secured(UserRole.Authority.USER)
//public class MemberApplicantController {
//
//    private final MemberApplicantService memberApplicantService;
//
//    @PostMapping("/v1/member-applicants/apply")
//    public ApiResponse<String> applyMember(
//            @AuthenticationPrincipal AuthUser authUser,
//            @RequestBody MemberRequest request
//    ) {
//        return ApiResponse.createSuccess(memberService.applyMember(authUser, request));
//    }
//
//    @PostMapping("/v1/member-applicants/accept")
//    public ApiResponse<MemberResponse> acceptMember(
//            @AuthenticationPrincipal AuthUser authUser,
//            @RequestBody MemberAcceptRequest request
//    ) {
//        return ApiResponse.createSuccess(memberService.acceptMember(authUser, request));
//    }
//
//    @PostMapping("/v1/member-applicants/reject")
//    public ApiResponse<String> rejectMember(
//            @AuthenticationPrincipal AuthUser authUser,
//            @RequestBody MemberRejectRequest request
//    ) {
//        memberService.rejectMember(authUser, request);
//        return ApiResponse.createSuccess(null);
//    }
//}
