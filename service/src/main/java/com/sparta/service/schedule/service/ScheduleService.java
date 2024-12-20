package com.sparta.service.schedule.service;

import com.sparta.common.user.dto.AuthUser;
import com.sparta.service.club.entity.Club;
import com.sparta.service.club.service.ClubService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeslotService timeslotService;
    private final ScheduleMessageService scheduleMessageService;
    private final ClubService clubService;
    private final StringRedisTemplate redisTemplate;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ElasticsearchService elasticsearchService;

    private static final String CHECK_AND_INCREMENT_SCRIPT = """
            local countKey = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local currentCount = redis.call('get', countKey)
      
            if currentCount then
                currentCount = tonumber(currentCount)
            else
                currentCount = 0
            end
            
            if currentCount < capacity then
                redis.call('incr', countKey)
                return 1
            else
                return 0
            end
            """;

    /**
     * 일정 생성
     *
     * @param authUser               : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param fitnessScheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @Transactional
    public ScheduleResponse createFitnessSchedule(AuthUser authUser,
                                                  FitnessScheduleRequest fitnessScheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(fitnessScheduleRequest.getTimeslotId());
        isExistsSchedule(authUser.getId(), timeslot.getStartTime());

        String countKey = "timeslot:count:" + timeslot.getId();

        // Lua 스크립트 실행
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(CHECK_AND_INCREMENT_SCRIPT, Long.class);
        Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(countKey),
                String.valueOf(timeslot.getCapacity())
        );

        if (result == 1) {
            Schedule newSchedule = Schedule.ofTimeslot(authUser.getId(), timeslot);
            Schedule savedSchedule = scheduleRepository.save(newSchedule);

            // Elasticsearch에 저장
            saveSearch(new ScheduleSearch(savedSchedule));

            // 알림 예약 (시작 시간 1시간 전)
            scheduleMessageService.scheduleNotification(authUser.getId(), timeslot.getStartTime(),
                    savedSchedule.getId());

            return new ScheduleResponse(savedSchedule);
        }

        return null;
    }

    @Transactional
    public ScheduleResponse createClubSchedule(AuthUser authUser,
                                               ClubScheduleRequest clubScheduleRequest) {
        Club club = clubService.isValidClub(clubScheduleRequest.getClubId());
        isExistsSchedule(authUser.getId(), club.getDate());

        Schedule newSchedule = Schedule.ofClub(authUser.getId(), club);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        // Elasticsearch에 저장
        saveSearch(new ScheduleSearch(savedSchedule));

        // 알림 예약 (시작 시간 1시간 전)
        scheduleMessageService.scheduleNotification(authUser.getId(), club.getDate(),
                savedSchedule.getId());

        return new ScheduleResponse(savedSchedule);
    }

    /**
     * 일정 수정
     *
     * @param authUser               : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleId             : 수정할 일정 ID
     * @param fitnessScheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @Transactional
    public ScheduleResponse updateFitnessSchedule(AuthUser authUser, long scheduleId,
                                                  FitnessScheduleRequest fitnessScheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(fitnessScheduleRequest.getTimeslotId());
        isExistsSchedule(authUser.getId(), timeslot.getStartTime());

        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateFitnessSchedule(timeslot);

        // Elasticsearch 업데이트
        saveSearch(new ScheduleSearch(schedule));

        // 기존 알림 취소 및 새로운 알림 등록
        scheduleMessageService.cancelScheduledNotification(scheduleId);
        scheduleMessageService.scheduleNotification(authUser.getId(), timeslot.getStartTime(), scheduleId);

        return new ScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateClubSchedule(AuthUser authUser, long scheduleId,
                                               ClubScheduleRequest clubScheduleRequest) {
        Club club = clubService.isValidClub(clubScheduleRequest.getClubId());
        isExistsSchedule(authUser.getId(), club.getDate());

        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateClubSchedule(club);

        // Elasticsearch 업데이트
        saveSearch(new ScheduleSearch(schedule));

        // 기존 알림 취소 및 새로운 알림 등록
        scheduleMessageService.cancelScheduledNotification(scheduleId);
        scheduleMessageService.scheduleNotification(authUser.getId(), club.getDate(), scheduleId);


        return new ScheduleResponse(schedule);
    }

    /**
     * 일정 삭제
     *
     * @param authUser   : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleId : 삭제할 일정 ID
     */
    @Transactional
    public void deleteSchedule(AuthUser authUser, long scheduleId) {
        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        // Elasticsearch에서 삭제
        deleteScheduleSearch(scheduleId);

        // 기존 알림 취소
        scheduleMessageService.cancelScheduledNotification(scheduleId);

        scheduleRepository.delete(schedule);
    }

    /**
     * 사용자별 일정 조회
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param year     : 조회할 연도
     * @param month    : 조회할 월
     * @param day      : 조회할 일
     * @return List<ScheduleResponse> : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO의 리스트
     */
    @Transactional(readOnly = true)
    public ScheduleListResponse getScheduleList(AuthUser authUser, Integer year, Integer month,
                                                Integer day) {
        log.info("Fetching schedules for user: {}, year: {}, month: {}, day: {}",
                authUser.getId(), year, month, day);

        year = (year != null) ? year : LocalDateTime.now().getYear();
        month = (month != null) ? month : LocalDateTime.now().getMonthValue();

        validateDate(month, day);

        // 쿼리 생성
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("userId", authUser.getId()))
                .must(QueryBuilders.termQuery("year", year))
                .must(QueryBuilders.termQuery("month", month));

        if (day != null) {
            queryBuilder.must(QueryBuilders.termQuery("day", day));
        }

        // Request Cache를 활용하는 검색 쿼리
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(Sort.by(Sort.Direction.ASC, "startTime"))
                .withPageable(PageRequest.of(0, 100))
                .withTrackTotalHits(true)
                .build();

        searchQuery.setRequestCache(true);    // Request Cache 활성화

        SearchHits<ScheduleSearch> searchHits = elasticsearchRestTemplate.search(
                searchQuery, ScheduleSearch.class);

        log.info("Found {} schedules", searchHits.getTotalHits());

        List<ScheduleResponse> scheduleResponseList = searchHits.stream()
                .map(hit -> new ScheduleResponse(
                        hit.getContent().getId(),
                        hit.getContent().getScheduleName(),
                        hit.getContent().getStartTime(),
                        hit.getContent().getEndTime(),
                        hit.getContent().getRequiredCoupon()
                ))
                .toList();

        int totalRequiredCoupon = scheduleResponseList.stream()
                .mapToInt(ScheduleResponse::getRequiredCoupon)
                .sum();

        return new ScheduleListResponse(scheduleResponseList, totalRequiredCoupon);
    }

    @Transactional
    public void copySchedule(AuthUser authUser, Integer yearToCopy
            , Integer monthToCopy, Integer copiedYear, Integer copiedMonth) {
        LocalDate startDateToCopy = LocalDate.of(yearToCopy, monthToCopy, 1);
        LocalDate endDateToCopy = LocalDate.of(yearToCopy, monthToCopy,
                startDateToCopy.lengthOfMonth());
        LocalDate copiedStartDate = LocalDate.of(copiedYear, copiedMonth, 1);
        LocalDate copiedEndDate = LocalDate.of(copiedYear, copiedMonth,
                copiedStartDate.lengthOfMonth());

        List<Schedule> scheduleToCopyList = scheduleRepository
                .findByUserIdAndClubIdIsNullAndStartTimeBetween(
                        authUser.getId(),
                        LocalDateTime.of(startDateToCopy, LocalTime.MIN),
                        LocalDateTime.of(endDateToCopy, LocalTime.MAX));

        LocalDate originalDate = scheduleToCopyList.get(0).getStartTime().toLocalDate();
        LocalDate originalLastDate = scheduleToCopyList
                .get(scheduleToCopyList.size() - 1).getStartTime().toLocalDate();
        LocalDate newDate = copiedStartDate.with(originalDate.getDayOfWeek());

        if (newDate.isBefore(copiedStartDate)) {
            newDate = newDate.plusWeeks(1);
        }

        for (Schedule schedule : scheduleToCopyList) {
            // 같은 요일을 찾기
            LocalDateTime newStartTime = LocalDateTime.of(newDate,
                    schedule.getStartTime().toLocalTime());
            newDate = newDate.plusDays(1);

            if (newDate.isAfter(copiedEndDate.with(originalLastDate.getDayOfWeek()))) {
                continue;
            }

            Schedule newSchedule = Schedule.fromOldSchedule(
                    schedule,
                    newStartTime);
            scheduleRepository.save(newSchedule);

            // Elasticsearch에 저장
            saveSearch(new ScheduleSearch(newSchedule));
        }
    }

    /**
     * 이미 존재하는 일정인지 확인
     *
     * @param userId    : 사용자 ID
     * @param startTime : 타임슬롯의 시작 시간
     */
    private void isExistsSchedule(long userId, LocalDateTime startTime) {
        if (scheduleRepository.existsByUserIdAndStartTime(userId, startTime)) {
            throw new ScheduleAlreadyExistsException();
        }
    }

    /**
     * 찾으려는 일정이 존재하는지 확인
     *
     * @param scheduleId : 찾으려는 일정 ID
     * @return Schedule : 일정 Entity
     */
    private Schedule isValidSchedule(long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(ScheduleNotFoundException::new);
    }

    /**
     * 일정의 주인과 사용자가 일치하는지 확인
     *
     * @param userId   : 사용자 ID
     * @param schedule : 일정 Entity
     */
    private void isScheduleOwner(long userId, Schedule schedule) {
        if (!schedule.getUserId().equals(userId)) {
            throw new NotScheduleOwnerException();
        }
    }

    private void validateDate(Integer month, Integer day) {
        if (!(month >= 1 && month <= 12)) {
            throw new InValidDateException();
        }
        if (day != null && !(day >= 1 && day <= 31)) {
            throw new InValidDateException();
        }
    }

    private void saveSearch(ScheduleSearch scheduleSearch) {
        elasticsearchService.saveSearch(scheduleSearch);
    }

    private void deleteScheduleSearch(Long scheduleId) {
        elasticsearchService.deleteSearch(String.valueOf(scheduleId), ScheduleSearch.class);
    }
}