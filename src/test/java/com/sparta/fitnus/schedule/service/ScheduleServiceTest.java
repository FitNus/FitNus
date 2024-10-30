package com.sparta.fitnus.schedule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sparta.fitnus.club.entity.Club;
import com.sparta.fitnus.club.service.ClubService;
import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.schedule.dto.request.ClubScheduleRequest;
import com.sparta.fitnus.schedule.dto.request.FitnessScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleListResponse;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.entity.Schedule;
import com.sparta.fitnus.schedule.exception.InValidDateException;
import com.sparta.fitnus.schedule.exception.NotScheduleOwnerException;
import com.sparta.fitnus.schedule.exception.ScheduleAlreadyExistsException;
import com.sparta.fitnus.schedule.exception.ScheduleNotFoundException;
import com.sparta.fitnus.schedule.repository.ScheduleRepository;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.timeslot.service.TimeslotService;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private TimeslotService timeslotService;

    @Mock
    private ClubService clubService;

    @Mock
    private ScheduleMessageService scheduleMessageService;

    @InjectMocks
    private ScheduleService scheduleService;

    @Nested
    class createFitnessSchedule {

        @Test
        void ScheduleAlreadyExistsException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(true);

            // when
            ScheduleAlreadyExistsException exception = assertThrows(ScheduleAlreadyExistsException.class
                    , () -> scheduleService.createFitnessSchedule(authUser, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule Already exists");
        }

        @Test
        void 성공() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();
            Fitness fitness = new Fitness();
            ReflectionTestUtils.setField(fitness, "fitnessName", "test");
            ReflectionTestUtils.setField(timeslot, "fitness", fitness);
            Schedule schedule = Schedule.ofTimeslot(authUser.getId(), timeslot);
            ReflectionTestUtils.setField(schedule, "id", 1L);

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.save(any())).willReturn(schedule);

            scheduleMessageService.scheduleNotification(authUser.getId(),timeslot.getStartTime());

            // when
            ScheduleResponse result = scheduleService.createFitnessSchedule(authUser, scheduleRequest);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class createClubSchedule {

        @Test
        void ScheduleAlreadyExistsException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(true);

            // when
            ScheduleAlreadyExistsException exception = assertThrows(ScheduleAlreadyExistsException.class
                    , () -> scheduleService.createClubSchedule(authUser, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule Already exists");
        }

        @Test
        void 성공() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();
            Schedule schedule = Schedule.ofClub(authUser.getId(), club);
            ReflectionTestUtils.setField(schedule, "id", 1L);

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.save(any())).willReturn(schedule);

            // when
            ScheduleResponse result = scheduleService.createClubSchedule(authUser, scheduleRequest);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class updateFitnessSchedule {

        @Test
        void ScheduleAlreadyExistsException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(true);

            // when
            ScheduleAlreadyExistsException exception = assertThrows(ScheduleAlreadyExistsException.class
                    , () -> scheduleService.updateFitnessSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule Already exists");
        }

        @Test
        void ScheduleNotFoundException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);

            // when
            ScheduleNotFoundException exception = assertThrows(ScheduleNotFoundException.class
                    , () -> scheduleService.updateFitnessSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule not found");
        }

        @Test
        void NotScheduleOwnerException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "userId", 2L);

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            NotScheduleOwnerException exception = assertThrows(NotScheduleOwnerException.class
                    , () -> scheduleService.updateFitnessSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Not schedule Owner");
        }

        @Test
        void 성공() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();
            Fitness fitness = new Fitness();
            ReflectionTestUtils.setField(fitness, "fitnessName", "test");
            ReflectionTestUtils.setField(timeslot, "fitness", fitness);
            Schedule schedule = Schedule.ofTimeslot(authUser.getId(), timeslot);
            ReflectionTestUtils.setField(schedule, "userId", 1L);

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            ScheduleResponse result = scheduleService.updateFitnessSchedule(authUser, 1L, scheduleRequest);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class updateClubSchedule {

        @Test
        void ScheduleAlreadyExistsException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(true);

            // when
            ScheduleAlreadyExistsException exception = assertThrows(ScheduleAlreadyExistsException.class
                    , () -> scheduleService.updateClubSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule Already exists");
        }

        @Test
        void ScheduleNotFoundException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);

            // when
            ScheduleNotFoundException exception = assertThrows(ScheduleNotFoundException.class
                    , () -> scheduleService.updateClubSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule not found");
        }

        @Test
        void NotScheduleOwnerException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "userId", 2L);

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            NotScheduleOwnerException exception = assertThrows(NotScheduleOwnerException.class
                    , () -> scheduleService.updateClubSchedule(authUser, 1L, scheduleRequest));

            // then
            assertThat(exception.getMessage()).isEqualTo("Not schedule Owner");
        }

        @Test
        void 성공() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            ClubScheduleRequest scheduleRequest = new ClubScheduleRequest(1L);
            Club club = new Club();
            Schedule schedule = Schedule.ofClub(authUser.getId(), club);
            ReflectionTestUtils.setField(schedule, "userId", 1L);

            given(clubService.isValidClub(scheduleRequest.getClubId())).willReturn(club);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            ScheduleResponse result = scheduleService.updateClubSchedule(authUser, 1L, scheduleRequest);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class deleteSchedule {

        @Test
        void ScheduleNotFoundException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");

            // when
            ScheduleNotFoundException exception = assertThrows(ScheduleNotFoundException.class
                    , () -> scheduleService.deleteSchedule(authUser, 1L));

            // then
            assertThat(exception.getMessage()).isEqualTo("Schedule not found");
        }

        @Test
        void NotScheduleOwnerException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "userId", 2L);

            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            NotScheduleOwnerException exception = assertThrows(NotScheduleOwnerException.class
                    , () -> scheduleService.deleteSchedule(authUser, 1L));

            // then
            assertThat(exception.getMessage()).isEqualTo("Not schedule Owner");
        }

        @Test
        void 삭제() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "userId", 1L);

            given(scheduleRepository.findById(anyLong())).willReturn(Optional.of(schedule));

            // when
            scheduleService.deleteSchedule(authUser, 1L);

            // then
            verify(scheduleRepository, times(1)).delete(schedule);
        }
    }

    @Nested
    class getScheduleList {

        @Test
        public void getScheduleList_ShouldReturnSchedules_WhenValidDate() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = 15;
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "requiredCoupon", 3);
            List<Schedule> scheduleList = List.of(schedule);

            given(scheduleRepository.findAllByUserIdYearAndMonthAndDay(authUser.getId(), year, month, day))
                    .willReturn(scheduleList);

            // when
            ScheduleListResponse response = scheduleService.getScheduleList(authUser, year, month, day);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        public void getScheduleList_ShouldReturnSchedules_WhenYearAndMonthAreNull() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = null;
            Integer month = null;
            Integer day = 15;
            Schedule schedule = new Schedule();
            ReflectionTestUtils.setField(schedule, "requiredCoupon", 3);
            List<Schedule> scheduleList = List.of(schedule);

            given(scheduleRepository.findAllByUserIdYearAndMonthAndDay(authUser.getId(),
                    LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonthValue(),
                    day)).willReturn(scheduleList);

            // when
            ScheduleListResponse response = scheduleService.getScheduleList(authUser, year, month, day);

            // when
            assertThat(response).isNotNull();
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenMonthIsInvalid() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 13;
            Integer day = null;

            // when & then
            assertThatThrownBy(() -> scheduleService.getScheduleList(authUser, year, month, day))
                    .isInstanceOf(InValidDateException.class);
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenMonthIsZero() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 0;
            Integer day = null;

            // when & then
            assertThatThrownBy(() -> scheduleService.getScheduleList(authUser, year, month, day))
                    .isInstanceOf(InValidDateException.class);
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenDayIsInvalid() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = 32;

            // when & then
            assertThatThrownBy(() -> scheduleService.getScheduleList(authUser, year, month, day))
                    .isInstanceOf(InValidDateException.class);
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenDayIsZero() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = 0;

            // when & then
            assertThatThrownBy(() -> scheduleService.getScheduleList(authUser, year, month, day))
                    .isInstanceOf(InValidDateException.class);
        }

        @Test
        public void getScheduleList_ShouldNotThrow_WhenDayIsValid() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = 15;

            // when
            ScheduleListResponse response = scheduleService.getScheduleList(authUser, year, month, day);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenDayIsNotInRange() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = -1;

            // when & then
            assertThatThrownBy(() -> scheduleService.getScheduleList(authUser, year, month, day))
                    .isInstanceOf(InValidDateException.class);
        }

        @Test
        public void getScheduleList_ShouldThrowInValidDateException_WhenDayIsNull() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            Integer year = 2023;
            Integer month = 10;
            Integer day = null;

            // when
            ScheduleListResponse response = scheduleService.getScheduleList(authUser, year, month, day);

            // then
            assertThat(response).isNotNull();
        }
    }
}