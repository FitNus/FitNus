package com.sparta.service.schedule.service;

import com.sparta.common.enums.UserRole;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.club.entity.Club;
import com.sparta.service.club.service.ClubService;
import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.schedule.dto.request.ClubScheduleRequest;
import com.sparta.service.schedule.dto.request.FitnessScheduleRequest;
import com.sparta.service.schedule.dto.response.ScheduleListResponse;
import com.sparta.service.schedule.dto.response.ScheduleResponse;
import com.sparta.service.schedule.entity.Schedule;
import com.sparta.service.schedule.entity.ScheduleSearch;
import com.sparta.service.schedule.exception.InValidDateException;
import com.sparta.service.schedule.exception.NotScheduleOwnerException;
import com.sparta.service.schedule.exception.ScheduleAlreadyExistsException;
import com.sparta.service.schedule.exception.ScheduleNotFoundException;
import com.sparta.service.schedule.repository.ScheduleRepository;
import com.sparta.service.search.service.ElasticsearchService;
import com.sparta.service.timeslot.entity.Timeslot;
import com.sparta.service.timeslot.service.TimeslotService;
import org.assertj.core.api.Assertions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    @Mock
    private ElasticsearchService elasticsearchService;

    @Mock
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private BoolQueryBuilder queryBuilder;

    private NativeSearchQuery searchQuery;

    private SearchHits<ScheduleSearch> searchHits;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        // 쿼리 생성
        queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("userId", 1L))
                .must(QueryBuilders.termQuery("year", 2023))
                .must(QueryBuilders.termQuery("month", 10))
                .must(QueryBuilders.termQuery("day", 15));

        // Request Cache를 활용하는 검색 쿼리
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(Sort.by(Sort.Direction.ASC, "startTime"))
                .withPageable(PageRequest.of(0, 100))
                .withTrackTotalHits(true)
                .build();

        searchHits = new SearchHits<ScheduleSearch>() {
            @Override
            public AggregationsContainer<?> getAggregations() {
                return null;
            }

            @Override
            public float getMaxScore() {
                return 0;
            }

            @Override
            public SearchHit<ScheduleSearch> getSearchHit(int index) {
                return null;
            }

            @Override
            public List<SearchHit<ScheduleSearch>> getSearchHits() {
                return List.of();
            }

            @Override
            public long getTotalHits() {
                return 0;
            }

            @Override
            public TotalHitsRelation getTotalHitsRelation() {
                return null;
            }

            @Override
            public Suggest getSuggest() {
                return null;
            }

            @Override
            public String getPointInTimeId() {
                return "";
            }
        };
    }

    @Nested
    class createFitnessSchedule {

        @Test
        void ScheduleAlreadyExistsException_발생() {
            // given
            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
            Timeslot timeslot = new Timeslot();
            ReflectionTestUtils.setField(timeslot, "id", 1L);
            ReflectionTestUtils.setField(timeslot, "capacity", 50);

            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(true);

            // when
            ScheduleAlreadyExistsException exception = assertThrows(ScheduleAlreadyExistsException.class
                    , () -> scheduleService.createFitnessSchedule(authUser, scheduleRequest));

            // then
            Assertions.assertThat(exception.getMessage()).isEqualTo("Schedule Already exists");
        }

//        @Test
//        void 성공() {
//            // given
//            AuthUser authUser = new AuthUser(1L, UserRole.USER, "test@test.com", "test");
//            FitnessScheduleRequest scheduleRequest = new FitnessScheduleRequest(1L);
//            Timeslot timeslot = new Timeslot();
//            Fitness fitness = new Fitness();
//            ReflectionTestUtils.setField(fitness, "fitnessName", "test");
//            ReflectionTestUtils.setField(timeslot, "fitness", fitness);
//            ReflectionTestUtils.setField(timeslot, "id", 1L);
//            ReflectionTestUtils.setField(timeslot, "maxPeople", 50);
//            Schedule schedule = Schedule.ofTimeslot(authUser.getId(), timeslot);
//            ReflectionTestUtils.setField(schedule, "id", 1L);
//
//            given(timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId())).willReturn(timeslot);
//            given(scheduleRepository.existsByUserIdAndStartTime(anyLong(), any())).willReturn(false);
//            given(scheduleRepository.save(any())).willReturn(schedule);
//
//            scheduleMessageService.scheduleNotification(authUser.getId(), timeslot.getStartTime(), schedule.getId());
//
//            // when
//            ScheduleResponse result = scheduleService.createFitnessSchedule(authUser, scheduleRequest);
//
//            // then
//            Assertions.assertThat(result).isNotNull();
//        }
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

            when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ScheduleSearch.class)))
                    .thenReturn(searchHits);

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

            when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ScheduleSearch.class)))
                    .thenReturn(searchHits);

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
            when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ScheduleSearch.class)))
                    .thenReturn(searchHits);
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
            when(elasticsearchRestTemplate.search(any(NativeSearchQuery.class), eq(ScheduleSearch.class)))
                    .thenReturn(searchHits);
            ScheduleListResponse response = scheduleService.getScheduleList(authUser, year, month, day);

            // then
            assertThat(response).isNotNull();
        }
    }
}