package com.sparta.fitnus.fitness.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.exception.AccessDeniedException;
import com.sparta.fitnus.fitness.exception.FitnessNotFoundException;
import com.sparta.fitnus.fitness.repository.FitnessRepository;
import com.sparta.fitnus.user.entity.AuthUser;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FitnessService {

    private final FitnessRepository fitnessRepository;
    private final CenterService centerService;

    /***
     * CRUD - POST 운동종목_등록_Api 연관 메소드 입니다.
     * @param authUser
     * @param request
     * @return FitnessResponse
     */
    @Transactional
    public FitnessResponse addFitness(AuthUser authUser, FitnessRequest request) {

        Center center = centerService.getCenterId(request.getCenterId());
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }
        Fitness fitness = Fitness.of(request, center);
        Fitness savedfitness = fitnessRepository.save(fitness);
        return new FitnessResponse(savedfitness);
    }


    /***
     * CRUD - GET 단건조회_Api_연관 메소드 입니다.
     * @param id
     * @return FitnessResponse
     */
    public FitnessResponse getFitness(Long id) {
        return fitnessRepository.findById(id)
                .map(FitnessResponse::new)
                .orElseThrow(FitnessNotFoundException::new);
    }

    /***
     * CRUD - GET 다건조회_Api_연관 메소드 입니다.
     * @return List<FitnessResponse>
     */
    public List<FitnessResponse> getAllFitness() {
        return fitnessRepository.findAll().stream()
                .map(FitnessResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FitnessResponse updateFitness(AuthUser authUser, Long fitnessId,
            FitnessRequest fitnessRequest) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new FitnessNotFoundException();
        }
        Center center = centerService.getCenterId(fitnessRequest.getCenterId());
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }
        Fitness fitness = isValidFitness(fitnessId);
        fitness.update(fitnessRequest);

        return new FitnessResponse(fitness);

    }


    /***
     * CRUD-DELETE : deleteCenter()의 기능입니다.
     * @param authUser
     * @param fitnessId
     */
    @Transactional
    public void deleteFitness(AuthUser authUser, Long fitnessId) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new FitnessNotFoundException();
        }
        Center center = centerService.getCenterId(isValidCenterInFitness(fitnessId));
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }

        fitnessRepository.deleteById(fitnessId);


    }

    public Long isValidCenterInFitness(Long fitnessId) {
        return fitnessRepository.findCenterIdByFitnessId(fitnessId).orElseThrow(FitnessNotFoundException::new);
    }

    public Fitness isValidFitness(Long fitnessId) {
        return fitnessRepository.findById(fitnessId).orElseThrow(FitnessNotFoundException::new);
    }
}


