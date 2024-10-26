package com.sparta.fitnus.schedule.service;

import com.sparta.fitnus.schedule.dto.request.ScheduleRequest;
import com.sparta.fitnus.schedule.dto.response.ScheduleResponse;
import com.sparta.fitnus.schedule.entity.Schedule;
import com.sparta.fitnus.schedule.repository.ScheduleRepository;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.timeslot.service.TimeslotService;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final TimeslotService timeslotService;

    @Transactional
    public ScheduleResponse createSchedule(AuthUser authUser, ScheduleRequest scheduleRequest) {
        User user = userService.getUser(authUser.getId());
        Timeslot timeslot = timeslotService.isValidTimeslot(scheduleRequest.getTimeslotId());

        Schedule newSchedule = Schedule.of(timeslot, user);
        Schedule savedSchedule = scheduleRepository.save(newSchedule);

        return new ScheduleResponse(savedSchedule);
    }
}
