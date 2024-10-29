package com.sparta.fitnus.timeslot.service;

import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.exception.AccessDeniedException;
import com.sparta.fitnus.fitness.service.FitnessService;
import com.sparta.fitnus.timeslot.dto.request.TimeslotRequest;
import com.sparta.fitnus.timeslot.dto.response.TimeslotResponse;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.timeslot.exception.TimeslotNotFoundException;
import com.sparta.fitnus.timeslot.repository.TimeslotRepository;
import com.sparta.fitnus.user.entity.AuthUser;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public TimeslotResponse createTimeslot(TimeslotRequest request) {
        Fitness fitness = fitnessService.isValidFitness(request.getFitnessId());
        Timeslot newTimeslot = Timeslot.of(request, fitness);

        Timeslot savedTimeslot = timeslotRepository.save(newTimeslot);

        return new TimeslotResponse(savedTimeslot);
    }

    public TimeslotResponse getTimeslot(Long timeslotId) {
        return timeslotRepository.findById(timeslotId)
                .map(TimeslotResponse::new)
                .orElseThrow(TimeslotNotFoundException::new);
    }

    public List<TimeslotResponse> getAllTimeslot() {
        return timeslotRepository.findAll().stream()
                .map(TimeslotResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTimeslot(AuthUser authUser, Long timeslotId){
        if (timeslotRepository.findById(timeslotId).isEmpty()) {
            throw new TimeslotNotFoundException();
        }
        Fitness fitness = fitnessService.isValidFitness(isValidFitnessInTimeslot(timeslotId));
        //이 fitness를 만든 fitness ID인지 체크
        if (authUser.getId().equals(fitness.getId())){
            throw new AccessDeniedException();
        }
        timeslotRepository.deleteById(timeslotId);
    }

    public Long isValidFitnessInTimeslot(Long timeslotId){
        return timeslotRepository.findFitnessIdByTimeslotId(timeslotId)
                .orElseThrow(TimeslotNotFoundException::new);
    }


    public Timeslot isValidTimeslot(long timeslotId) {
        return timeslotRepository.findById(timeslotId).orElseThrow(TimeslotNotFoundException::new);
    }
}
