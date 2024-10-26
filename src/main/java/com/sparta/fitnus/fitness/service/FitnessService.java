package com.sparta.fitnus.fitness.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.repository.FitnessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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



    /***
     * CRUD - GET 단건조회 Api연관 메소드 입니다.
     * @param id
     * @return
     */
    public FitnessResponse getFitness(Long id) {
        return fitnessRepository.findById(id)
                .map(FitnessResponse::new)
                .orElseThrow(() -> new NotFoundException("Fitness with id " + id + " not found"));
    }


    /***
     * CRUD - GET 다건조회
     */
    public List<FitnessResponse> serchFitness() {
        return fitnessRepository.findAll().stream()
                .map(FitnessResponse::new)
                .collect(Collectors.toList());
    }

    // 피트니스 유효성 검사하고, 피트니스 id 가저갈 수 있게 service에서 만들기

    public Fitness isValidFitness(Long fitnessId) {
        return fitnessRepository.findById(fitnessId).orElseThrow(() ->
                new NotFoundException("Fitness not found"));
    }

}
