package com.sparta.fitnus.fitness.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.common.exception.FitnessNotFoundException;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.repository.FitnessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FitnessService {
    private final FitnessRepository fitnessRepository;
    private final CenterService centerService;

    @Transactional
    public FitnessResponse addFitness(FitnessRequest request, Long id) {
        Center center = centerService.getCenterId(id);
        Fitness fitness = Fitness.of(request, center);
        Fitness savedfitness = fitnessRepository.save(fitness);
        return new FitnessResponse(savedfitness);
    }

    public Fitness isValidFitness(long fitnessId) {
        return fitnessRepository.findById(fitnessId).orElseThrow(FitnessNotFoundException::new);
    }
}
