package com.sparta.service.timeslot.service;

import com.sparta.common.dto.AuthUser;
import com.sparta.common.enums.UserRole;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.service.CenterService;
import com.sparta.service.fitness.entity.Fitness;
import com.sparta.service.fitness.exception.AccessDeniedException;
import com.sparta.service.fitness.service.FitnessService;
import com.sparta.service.timeslot.dto.request.TimeslotDeleteRequest;
import com.sparta.service.timeslot.dto.request.TimeslotRequest;
import com.sparta.service.timeslot.dto.response.TimeslotResponse;
import com.sparta.service.timeslot.entity.Timeslot;
import com.sparta.service.timeslot.exception.TimeslotNotFoundException;
import com.sparta.service.timeslot.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final FitnessService fitnessService;
    private final CenterService centerService;

    /***
     * CRUD-POST : createTimeslot()의 기능입니다.
     * @param authUser
     * @param request
     * @return TimeslotResponse
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public TimeslotResponse createTimeslot(AuthUser authUser, TimeslotRequest request) {
        Center center = centerService.getCenterId(request.getCenterId());
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }
        Fitness fitness = fitnessService.isValidFitness(request.getFitnessId());
        Timeslot newTimeslot = Timeslot.of(request, fitness);

        Timeslot savedTimeslot = timeslotRepository.save(newTimeslot);

        return new TimeslotResponse(savedTimeslot);
    }

    /***
     * CRUD-GET(단건조회) : getTimeslot()의 기능입니다.
     * @param timeslotId
     * @return TimeslotResponse
     */
    public TimeslotResponse getTimeslot(Long timeslotId) {
        return timeslotRepository.findById(timeslotId)
                .map(TimeslotResponse::new)
                .orElseThrow(TimeslotNotFoundException::new);
    }

    /***
     * CRUD-GET(다건조회) : getAllTimeslot()의 기능입니다.
     * @return List<TimeslotResponse>
     */
    public List<TimeslotResponse> getAllTimeslot() {
        return timeslotRepository.findAll().stream()
                .map(TimeslotResponse::new)
                .collect(Collectors.toList());
    }

    /***
     * CRUD-DELETE : deleteTimeslot()의 기능입니다.
     * @param authUser
     * @param timeslotId
     * @param request
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public void deleteTimeslot(AuthUser authUser, Long timeslotId, TimeslotDeleteRequest request) {
        if (timeslotRepository.findById(timeslotId).isEmpty()) {
            throw new TimeslotNotFoundException();
        }
        if (!authUser.getId().equals(request.getCenterId())) {
            throw new AccessDeniedException();
        }
        timeslotRepository.deleteById(timeslotId);
    }

    public Timeslot isValidTimeslot(long timeslotId) {
        return timeslotRepository.findById(timeslotId).orElseThrow(TimeslotNotFoundException::new);
    }
}

