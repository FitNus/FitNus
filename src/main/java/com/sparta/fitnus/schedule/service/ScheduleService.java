package com.sparta.fitnus.schedule.service;

import com.sparta.fitnus.common.exception.ScheduleNotFoundException;
import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.entity.Schedule;
import com.sparta.fitnus.schedule.exception.InValidDateException;
import com.sparta.fitnus.schedule.exception.NotScheduleOwnerException;
import com.sparta.fitnus.schedule.exception.TimeslotAlreadyExistsException;
import com.sparta.fitnus.schedule.repository.ScheduleRepository;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.timeslot.service.TimeslotService;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final TimeslotService timeslotService;

    /**
     * 일정 생성
     *
     * @param authUser        : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @Transactional
    public ScheduleResponse createSchedule(AuthUser authUser, ScheduleRequest scheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId());
        isExistsTimeslot(authUser.getId(), timeslot.getStartTime());

        Schedule newSchedule = Schedule.of(authUser.getId(), timeslot);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        return new ScheduleResponse(savedSchedule);
    }

    /**
     * 일정 수정
     *
     * @param authUser        : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleId      : 수정할 일정 ID
     * @param scheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @Transactional
    public ScheduleResponse updateSchedule(AuthUser authUser, long scheduleId, ScheduleRequest scheduleRequest) {
        Timeslot timeslot = timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId());
        isExistsTimeslot(authUser.getId(), timeslot.getStartTime());

        Schedule schedule = isValidSchedule(scheduleId);
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateSchedule(timeslot);

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
    public List<ScheduleResponse> getScheduleList(AuthUser authUser, Integer year, Integer month, Integer day) {
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

        return scheduleList.stream().map(ScheduleResponse::new).toList();
    }

    /**
     * 이미 존재하는 일정인지 확인
     *
     * @param userId    : 사용자 ID
     * @param startTime : 타임슬롯의 시작 시간
     */
    private void isExistsTimeslot(long userId, LocalDateTime startTime) {
        if (scheduleRepository.existsByUserIdAndStartTime(userId, startTime)) {
            throw new TimeslotAlreadyExistsException();
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