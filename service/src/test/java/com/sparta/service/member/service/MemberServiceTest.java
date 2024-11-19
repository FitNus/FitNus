package com.sparta.service.member.service;

import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.club.entity.Club;
import com.sparta.service.club.service.ClubService;
import com.sparta.service.member.dto.request.MemberDeportRequest;
import com.sparta.service.member.dto.request.MemberRequest;
import com.sparta.service.member.dto.response.MemberResponse;
import com.sparta.service.member.entity.Member;
import com.sparta.service.member.exception.AlreadyMemberException;
import com.sparta.service.member.exception.CanNotDeportLeaderException;
import com.sparta.service.member.exception.MemberNotFound;
import com.sparta.service.member.exception.NotLeaderException;
import com.sparta.service.member.repository.MemberRepository;
import com.sparta.user.user.entity.User;
import com.sparta.user.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MemberService memberService;

    @Nested
    class getMemberList {

        @Test
        void 성공() {
            //given
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            Member member = new Member();
            ReflectionTestUtils.setField(member, "userId", 1L);
            List<Member> memberList = new ArrayList<>();
            memberList.add(member);
            Page<Member> memberPage = new PageImpl<>(memberList);
            Club club = new Club();
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.findAllByClub(any(), any())).willReturn(memberPage);
            given(userService.getUser(anyLong())).willReturn(user);

            //when
            Page<MemberResponse> responses = memberService.getMemberList(1, 10, memberRequest);

            //then
            assertThat(responses).isNotNull();
        }
    }

    @Nested
    class withdrawMember {

        @Test
        void MemberNotFound_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            Club club = new Club();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(false);

            //when
            MemberNotFound exception = assertThrows(MemberNotFound.class
                    , () -> memberService.withdrawMember(authUser, memberRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Member not found");
        }

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            Club club = new Club();
            ReflectionTestUtils.setField(club, "id", 1L);
            ReflectionTestUtils.setField(club, "leaderId", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(true);

            //when
            memberService.withdrawMember(authUser, memberRequest);

            //then
            verify(memberRepository, times(1)).deleteByClubAndUserId(club, authUser.getId());
        }
    }

    @Nested
    class deportMember {

        @Test
        void NotLeaderException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberDeportRequest memberDeportRequest = new MemberDeportRequest();
            ReflectionTestUtils.setField(memberDeportRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberDeportRequest, "userId", 1L);
            Club club = new Club();
            ReflectionTestUtils.setField(club, "id", 1L);
            ReflectionTestUtils.setField(club, "leaderId", 2L);

            given(clubService.isValidClub(anyLong())).willReturn(club);

            //when
            NotLeaderException exception = assertThrows(NotLeaderException.class
                    , () -> memberService.deportMember(authUser, memberDeportRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Not leader");
        }

        @Test
        void MemberNotFound_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberDeportRequest memberDeportRequest = new MemberDeportRequest();
            ReflectionTestUtils.setField(memberDeportRequest, "clubId", 1L);
            Club club = new Club();
            ReflectionTestUtils.setField(club, "leaderId", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(false);

            //when
            MemberNotFound exception = assertThrows(MemberNotFound.class
                    , () -> memberService.deportMember(authUser, memberDeportRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Member not found");
        }

        @Test
        void CanNotDeportLeaderException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberDeportRequest memberDeportRequest = new MemberDeportRequest();
            ReflectionTestUtils.setField(memberDeportRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberDeportRequest, "userId", 1L);
            Club club = new Club();
            ReflectionTestUtils.setField(club, "id", 1L);
            ReflectionTestUtils.setField(club, "leaderId", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(true);

            //when
            CanNotDeportLeaderException exception = assertThrows(CanNotDeportLeaderException.class
                    , () -> memberService.deportMember(authUser, memberDeportRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Can not deport leader");
        }

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberDeportRequest memberDeportRequest = new MemberDeportRequest();
            ReflectionTestUtils.setField(memberDeportRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberDeportRequest, "userId", 2L);
            Club club = new Club();
            ReflectionTestUtils.setField(club, "id", 1L);
            ReflectionTestUtils.setField(club, "leaderId", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(true);

            //when
            memberService.deportMember(authUser, memberDeportRequest);

            //then
            verify(memberRepository, times(1)).deleteByClubAndUserId(club, memberDeportRequest.getUserId());
        }
    }

    @Nested
    class isAlreadyMember {

        @Test
        void AlreadyMemberException_발생() {
            //given
            Club club = new Club();

            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(true);

            //when
            AlreadyMemberException exception = assertThrows(AlreadyMemberException.class
                    , () -> memberService.isAlreadyMember(club, 1L));

            //then
            assertThat(exception.getMessage()).isEqualTo("Already member");
        }

        @Test
        void 성공() {
            //given
            Club club = new Club();

            given(memberRepository.existsByClubAndUserId(any(), anyLong())).willReturn(false);

            //when
            memberService.isAlreadyMember(club, 1L);

            //then
            verify(memberRepository, times(1)).existsByClubAndUserId(club, 1L);
        }
    }
}