package com.sparta.modulecommon.schedule.service;

import com.sparta.modulecommon.club.entity.Club;
import com.sparta.modulecommon.club.service.ClubService;
import com.sparta.modulecommon.common.annotation.DistributedLock;
import com.sparta.modulecommon.schedule.dto.request.ClubScheduleRequest;
import com.sparta.modulecommon.schedule.dto.request.FitnessScheduleRequest;
import com.sparta.modulecommon.schedule.dto.response.ScheduleListResponse;
import com.sparta.modulecommon.schedule.dto.response.ScheduleResponse;
import com.sparta.modulecommon.schedule.entity.Schedule;
import com.sparta.modulecommon.schedule.exception.InValidDateException;
import com.sparta.modulecommon.schedule.exception.NotScheduleOwnerException;
import com.sparta.modulecommon.schedule.exception.ScheduleAlreadyExistsException;
import com.sparta.modulecommon.schedule.exception.ScheduleNotFoundException;
import com.sparta.modulecommon.schedule.repository.ScheduleRepository;
import com.sparta.modulecommon.timeslot.entity.Timeslot;
import com.sparta.modulecommon.timeslot.service.TimeslotService;
import com.sparta.modulecommon.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeslotService timeslotService;
    private final ScheduleMessageService scheduleMessageService;
    private final ClubService clubService;

    /**
     * 일정 생성
     *
     * @param authUser               : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param fitnessScheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @DistributedLock(key = "'timeslot:' + #fitnessScheduleRequest.timeslotId")
    @Transactional
    public ScheduleResponse createFitnessSchedule(AuthUser authUser, FitnessScheduleRequest fitnessScheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(fitnessScheduleRequest.getTimeslotId());

        if (timeslot.getMaxPeople() > scheduleRepository.countByTimeslotId(timeslot.getId()) + 1) {
            isExistsSchedule(authUser.getId(), timeslot.getStartTime());

            Schedule newSchedule = Schedule.ofTimeslot(authUser.getId(), timeslot);
            Schedule savedSchedule = scheduleRepository.save(newSchedule);

            // 알림 예약 (시작 시간 1시간 전)
            scheduleMessageService.scheduleNotification(authUser.getId(), timeslot.getStartTime(), savedSchedule.getId());

            return new ScheduleResponse(savedSchedule);
        } else {
            return null;
        }
    }

    @Transactional
    public ScheduleResponse createClubSchedule(AuthUser authUser, ClubScheduleRequest clubScheduleRequest) {
        Club club = clubService.isValidClub(clubScheduleRequest.getClubId());
        isExistsSchedule(authUser.getId(), club.getDate());

        Schedule newSchedule = Schedule.ofClub(authUser.getId(), club);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        // 알림 예약 (시작 시간 1시간 전)
        scheduleMessageService.scheduleNotification(authUser.getId(), club.getDate(), savedSchedule.getId());

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
    public ScheduleResponse updateFitnessSchedule(AuthUser authUser, long scheduleId, FitnessScheduleRequest fitnessScheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(fitnessScheduleRequest.getTimeslotId());
        isExistsSchedule(authUser.getId(), timeslot.getStartTime());
//        isFullTimeslot(timeslot);

        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateFitnessSchedule(timeslot);

        return new ScheduleResponse(schedule);
    }

    @Transactional
    public ScheduleResponse updateClubSchedule(AuthUser authUser, long scheduleId, ClubScheduleRequest clubScheduleRequest) {
        Club club = clubService.isValidClub(clubScheduleRequest.getClubId());
        isExistsSchedule(authUser.getId(), club.getDate());

        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateClubSchedule(club);

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
    public ScheduleListResponse getScheduleList(AuthUser authUser, Integer year, Integer month, Integer day) {
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        if (month == null) {
            month = LocalDateTime.now().getMonthValue();
        }
        if (!(month >= 1 & month <= 12)) {
            throw new InValidDateException();
        }
        if (day != null) {
            if (!(day >= 1 & day <= 31)) {
                throw new InValidDateException();
            }
        }

        List<Schedule> scheduleList = scheduleRepository.findAllByUserIdYearAndMonthAndDay(authUser.getId(), year, month, day);

        List<ScheduleResponse> scheduleResponseList = scheduleList.stream()
            .map(ScheduleResponse::new)
            .toList();

        int sum = scheduleList.stream()
            .mapToInt(Schedule::getRequiredCoupon)
            .sum();

        return new ScheduleListResponse(scheduleResponseList, sum);
    }

    @Transactional
    public void copySchedule(AuthUser authUser, Integer yearToCopy
        , Integer monthToCopy, Integer copiedYear, Integer copiedMonth) {
        LocalDate startDateToCopy = LocalDate.of(yearToCopy, monthToCopy, 1);
        LocalDate endDateToCopy = LocalDate.of(yearToCopy, monthToCopy, startDateToCopy.lengthOfMonth());
        LocalDate copiedStartDate = LocalDate.of(copiedYear, copiedMonth, 1);
        LocalDate copiedEndDate = LocalDate.of(copiedYear, copiedMonth, copiedStartDate.lengthOfMonth());

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
            LocalDateTime newStartTime = LocalDateTime.of(newDate, schedule.getStartTime().toLocalTime());
            newDate = newDate.plusDays(1);

            if (newDate.isAfter(copiedEndDate.with(originalLastDate.getDayOfWeek()))) {
                continue;
            }

            Schedule newSchedule = Schedule.fromOldSchedule(
                schedule,
                newStartTime);
            scheduleRepository.save(newSchedule);
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
}