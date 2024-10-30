package com.sparta.fitnus.applicant.service;

import com.sparta.fitnus.applicant.dto.response.MemberApplicantResponse;
import com.sparta.fitnus.applicant.entity.MemberApplicant;
import com.sparta.fitnus.applicant.exception.AlreadyApplyException;
import com.sparta.fitnus.applicant.exception.MemberApplicantNotFoundException;
import com.sparta.fitnus.applicant.repository.MemberApplicantsRepository;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.member.dto.request.MemberAcceptRequest;
import com.sparta.fitnus.member.dto.request.MemberRejectRequest;
import com.sparta.fitnus.member.dto.request.MemberRequest;
import com.sparta.fitnus.member.entity.Member;
import com.sparta.fitnus.member.repository.MemberRepository;
import com.sparta.fitnus.member.service.MemberService;
import com.sparta.fitnus.ssenotification.service.SseNotificationServiceImpl;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.service.UserService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberApplicantServiceTest {

    @Mock
    private MemberApplicantsRepository memberApplicantsRepository;

    @Mock
    private ClubService clubService;

    @Mock
    private UserService userService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private SseNotificationServiceImpl sseNotificationServiceImpl;

    @InjectMocks
    private MemberApplicantService memberApplicantService;

    @Nested
    class createMemberApplicant {

        @Test
        void AlreadyApplyException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            Club club = new Club();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.existsByClubAndUserId(any(), anyLong())).willReturn(true);

            //when
            AlreadyApplyException exception = assertThrows(AlreadyApplyException.class
                    , () -> memberApplicantService.createMemberApplicant(authUser, memberRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Already apply member");
        }

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            Club club = new Club();
            MemberApplicant memberApplicant = new MemberApplicant();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.existsByClubAndUserId(any(), anyLong())).willReturn(false);
            memberApplicantsRepository.save(memberApplicant);

            //when
            memberApplicantService.createMemberApplicant(authUser, memberRequest);

            //then
            verify(memberApplicantsRepository, times(1)).save(memberApplicant);
        }
    }

    @Nested
    class acceptMemberApplicant {

        @Test
        void MemberApplicantNotFoundException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberAcceptRequest memberAcceptRequest = new MemberAcceptRequest();
            ReflectionTestUtils.setField(memberAcceptRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberAcceptRequest, "userId", 1L);
            Club club = new Club();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.findByClubAndUserId(any(), anyLong())).willReturn(Optional.empty());

            //when
            MemberApplicantNotFoundException exception = assertThrows(MemberApplicantNotFoundException.class
                    , () -> memberApplicantService.acceptMemberApplicant(authUser, memberAcceptRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Member applicant not found");
        }

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberAcceptRequest memberAcceptRequest = new MemberAcceptRequest();
            ReflectionTestUtils.setField(memberAcceptRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberAcceptRequest, "userId", 1L);
            Club club = new Club();
            MemberApplicant memberApplicant = new MemberApplicant();
            ReflectionTestUtils.setField(memberApplicant, "id", 1L);
            Member member = Member.of(memberApplicant);
            club.getMemberList().add(member);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.findByClubAndUserId(club, memberAcceptRequest.getUserId())).willReturn(Optional.of(memberApplicant));
            memberApplicantsRepository.save(memberApplicant);

            //when
            memberApplicantService.acceptMemberApplicant(authUser, memberAcceptRequest);

            //then
            verify(memberApplicantsRepository, times(1)).delete(memberApplicant);
            assertTrue(club.getMemberList().contains(member));
        }
    }

    @Nested
    class rejectMemberApplicant {

        @Test
        void MemberApplicantNotFoundException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRejectRequest memberRejectRequest = new MemberRejectRequest();
            ReflectionTestUtils.setField(memberRejectRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberRejectRequest, "userId", 1L);
            Club club = new Club();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.findByClubAndUserId(any(), anyLong())).willReturn(Optional.empty());

            //when
            MemberApplicantNotFoundException exception = assertThrows(MemberApplicantNotFoundException.class
                    , () -> memberApplicantService.rejectMemberApplicant(authUser, memberRejectRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Member applicant not found");
        }

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRejectRequest memberRejectRequest = new MemberRejectRequest();
            ReflectionTestUtils.setField(memberRejectRequest, "clubId", 1L);
            ReflectionTestUtils.setField(memberRejectRequest, "userId", 1L);
            Club club = new Club();
            MemberApplicant memberApplicant = new MemberApplicant();

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.findByClubAndUserId(club, memberRejectRequest.getUserId())).willReturn(Optional.of(memberApplicant));
            memberApplicantsRepository.save(memberApplicant);

            //when
            memberApplicantService.rejectMemberApplicant(authUser, memberRejectRequest);

            //then
            verify(memberApplicantsRepository, times(1)).delete(memberApplicant);
        }
    }

    @Nested
    class getMemberApplicantList {

        @Test
        void 성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            MemberRequest memberRequest = new MemberRequest();
            ReflectionTestUtils.setField(memberRequest, "clubId", 1L);
            MemberApplicant memberApplicant = new MemberApplicant();
            ReflectionTestUtils.setField(memberApplicant, "userId", 1L);
            List<MemberApplicant> memberApplicantList = new ArrayList<>();
            memberApplicantList.add(memberApplicant);
            Page<MemberApplicant> memberApplicantPage = new PageImpl<>(memberApplicantList);
            Club club = new Club();
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);

            given(clubService.isValidClub(anyLong())).willReturn(club);
            given(memberApplicantsRepository.findAllByClub(any(), any())).willReturn(memberApplicantPage);
            given(userService.getUser(anyLong())).willReturn(user);

            //when
            Page<MemberApplicantResponse> responses = memberApplicantService.getMemberApplicantList(authUser, 1, 10, memberRequest);

            //then
            assertThat(responses).isNotNull();
        }
    }
}