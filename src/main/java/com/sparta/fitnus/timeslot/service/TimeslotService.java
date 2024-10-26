package com.sparta.fitnus.timeslot.service;

import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.service.FitnessService;
import com.sparta.fitnus.timeslot.dto.request.TimeslotRequest;
import com.sparta.fitnus.timeslot.dto.response.TimeslotResponse;
import com.sparta.fitnus.timeslot.entity.Timeslot;
import com.sparta.fitnus.timeslot.repository.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final FitnessService fitnessService;

    public TimeslotResponse createTimeslot(TimeslotRequest request) {
        Fitness fitness = fitnessService.isValidFitness(request.getFitnessId());
        Timeslot newTimeslot = Timeslot.of(request, fitness);

        Timeslot savedTimeslot = timeslotRepository.save(newTimeslot);

        return new TimeslotResponse(savedTimeslot);
    }
}
