package com.sparta.fitnus.club.service;

import com.sparta.fitnus.club.dto.request.ClubRequest;
import com.sparta.fitnus.club.dto.response.ClubResponse;
import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.exception.AlreadyExistsClubNameException;
import com.sparta.fitnus.club.exception.ClubNotFoundException;
import com.sparta.fitnus.club.repository.ClubRepository;
import com.sparta.fitnus.member.exception.NotLeaderException;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    @InjectMocks
    private ClubService clubService;

    @Nested
    class createClub {

        @Test
        void createClub_AlreadyExistsClubNameException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());

            given(clubRepository.existsByClubName(any())).willReturn(true);

            //when
            AlreadyExistsClubNameException exception = assertThrows(AlreadyExistsClubNameException.class
                    , () -> clubService.createClub(authUser, clubRequest));

            //then
            assertThat(exception.getMessage()).isEqualTo("Already exists club name");
        }

        @Test
        void createClub_성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());

            given(clubRepository.existsByClubName(any())).willReturn(false);
            given(clubRepository.save(any())).willReturn(club);

            //when
            ClubResponse result = clubService.createClub(authUser, clubRequest);

            //then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class updateClub {

        @Test
        void updateClub_AlreadyExistsClubNameException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.existsByClubName(any())).willReturn(true);

            //when
            AlreadyExistsClubNameException exception = assertThrows(AlreadyExistsClubNameException.class
                    , () -> clubService.updateClub(authUser, clubRequest, club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Already exists club name");
        }

        @Test
        void updateClub_ClubNotFoundException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.existsByClubName(any())).willReturn(false);
            given(clubRepository.findById(anyLong())).willReturn(Optional.empty());

            //when
            ClubNotFoundException exception = assertThrows(ClubNotFoundException.class
                    , () -> clubService.updateClub(authUser, clubRequest, club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Club not found");
        }

        @Test
        void updateClub_NotLeaderException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, 2L);
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.existsByClubName(any())).willReturn(false);
            given(clubRepository.findById(anyLong())).willReturn(Optional.of(club));

            //when
            NotLeaderException exception = assertThrows(NotLeaderException.class
                    , () -> clubService.updateClub(authUser, clubRequest, club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Not leader");
        }

        @Test
        void updateClub_성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.existsByClubName(any())).willReturn(false);
            given(clubRepository.findById(anyLong())).willReturn(Optional.of(club));

            //when
            ClubResponse result = clubService.updateClub(authUser, clubRequest, club.getId());

            //then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class getClub {

        @Test
        void updateClub_ClubNotFoundException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.findById(anyLong())).willReturn(Optional.empty());

            //when
            ClubNotFoundException exception = assertThrows(ClubNotFoundException.class
                    , () -> clubService.getClub(club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Club not found");
        }

        @Test
        void getClub_성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.findById(anyLong())).willReturn(Optional.of(club));

            //when
            ClubResponse result = clubService.getClub(club.getId());

            //then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class deleteClub {

        @Test
        void deleteClub_ClubNotFoundException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.findById(anyLong())).willReturn(Optional.empty());

            //when
            ClubNotFoundException exception = assertThrows(ClubNotFoundException.class
                    , () -> clubService.deleteClub(authUser, club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Club not found");
        }

        @Test
        void deleteClub_NotLeaderException_발생() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, 2L);
            ReflectionTestUtils.setField(club, "id", 1L);

            given(clubRepository.findById(anyLong())).willReturn(Optional.of(club));

            //when
            NotLeaderException exception = assertThrows(NotLeaderException.class
                    , () -> clubService.deleteClub(authUser, club.getId()));

            //then
            assertThat(exception.getMessage()).isEqualTo("Not leader");
        }

        @Test
        void deleteClub_성공() {
            //given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubRequest clubRequest = new ClubRequest("test", "test", "test", LocalDateTime.now());
            Club club = Club.of(clubRequest, authUser.getId());
            ReflectionTestUtils.setField(club, "id", 1L);
            
            given(clubRepository.findById(anyLong())).willReturn(Optional.of(club));

            //when
            clubService.deleteClub(authUser, club.getId());

            //then
            verify(clubRepository, times(1)).delete(club);
        }
    }
}