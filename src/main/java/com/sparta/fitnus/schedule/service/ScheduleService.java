package com.sparta.fitnus.schedule.service;

import com.sparta.fitnus.common.exception.NotScheduleOwnerException;
import com.sparta.fitnus.common.exception.ScheduleNotFoundException;
import com.sparta.fitnus.common.exception.TimeslotAlreadyExistsException;
import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.entity.Schedule;
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
    private final ScheduleMessageService scheduleMessageService;

    /**
     * 일정 생성
     *
     * @param authUser        : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleRequest : 타임슬롯 ID를 담고 있는 DTO
     * @return ScheduleResponse : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO
     */
    @Transactional
    public ScheduleResponse createSchedule(AuthUser authUser, ScheduleRequest scheduleRequest) {
        // 이미 존재하는 일정인지 확인
        isExistsTimeslot(authUser.getId(), scheduleRequest.getTimeslotId());
        // timeslotId와 일치하는 timeslot이 있는지 timeslot이 삭제되진 않았는지 확인
        Timeslot timeslot = timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId());

        Schedule newSchedule = Schedule.of(authUser.getId(), timeslot);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        // 알림 예약 (시작 시간 1시간 전)
        scheduleMessageService.scheduleNotification(authUser.getId(),timeslot.getStartTime());

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
        // 이미 존재하는 일정인지 확인
        isExistsTimeslot(authUser.getId(), scheduleRequest.getTimeslotId());
        // timeslotId와 일치하는 timeslot이 있는지 timeslot이 삭제되진 않았는지 확인
        Timeslot timeslot = timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId());

        // scheduleId와 일치하는 shedule이 있는지 확인
        Schedule schedule = isValidSchedule(scheduleId);
        // schedule을 생성한 userId와 schedule을 수정하려는 사람의 userId가 일치 하는지 확인
        isScheduleOwner(authUser.getId(), schedule);

        schedule.updateSchedule(timeslot);

        return new ScheduleResponse(schedule);
    }

    /**
     * 일정 삭제
     *
     * @param authUser   : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param scheduleId : 삭제할 일정 ID
     * @return String : API 성공 응답메세지
     */
    @Transactional
    public String deleteSchedule(AuthUser authUser, long scheduleId) {
        // scheduleId와 일치하는 shedule이 있는지 확인
        Schedule schedule = isValidSchedule(scheduleId);

        // schedule을 생성한 userId와 schedule을 삭제하려는 사람의 userId가 일치 하는지 확인
        isScheduleOwner(authUser.getId(), schedule);

        scheduleRepository.delete(schedule);

        return "일정이 정상적으로 삭제되었습니다.";
    }

    /**
     * 일정 사용자별 월 단위 조회
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param month    : 조회할 월
     * @return List<ScheduleResponse> : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO의 리스트
     */
    public List<ScheduleResponse> getMonthlyScheduleList(AuthUser authUser, Integer month) {
        if (month == null) {
            month = LocalDateTime.now().getMonthValue();
        }

        List<Schedule> scheduleList = scheduleRepository.findAllByUserIdAndMonth(authUser.getId(), month);

        return scheduleList.stream().map(ScheduleResponse::new).toList();
    }

    /**
     * 일정 사용자별 일 단위 조회
     *
     * @param authUser : 사용자 ID, 사용자 권한, email, nickname을 담고 있는 객체
     * @param month    : 조회할 월
     * @param day      : 조회할 일
     * @return List<ScheduleResponse> : 일정 ID, 운동 종목, 시작 시간, 끝나는 시간, 가격을 담고 있는 DTO의 리스트
     */
    public List<ScheduleResponse> getDailyScheduleList(AuthUser authUser, Integer month, Integer day) {
        if (month == null) {
            month = LocalDateTime.now().getMonthValue();
        }

        if (day == null) {
            day = LocalDateTime.now().getDayOfMonth();
        }

        List<Schedule> scheduleList = scheduleRepository.findAllByUserIdAndMonthAndDay(authUser.getId(), month, day);

        return scheduleList.stream().map(ScheduleResponse::new).toList();
    }

    /**
     * 이미 존재하는 일정인지 확인
     *
     * @param userId     : 사용자 ID
     * @param timeslotId : 타임슬롯 ID
     */
    private void isExistsTimeslot(long userId, long timeslotId) {
        if (scheduleRepository.existsByUserIdAndTimeslotId(userId, timeslotId)) {
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