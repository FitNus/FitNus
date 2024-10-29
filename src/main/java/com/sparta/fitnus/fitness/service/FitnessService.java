package com.sparta.fitnus.fitness.service;

import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.service.CenterService;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.fitness.dto.request.FitnessRequest;
import com.sparta.fitnus.fitness.dto.response.FitnessResponse;
import com.sparta.fitnus.fitness.entity.Fitness;
import com.sparta.fitnus.fitness.exception.AccessDeniedException;
import com.sparta.fitnus.fitness.repository.FitnessRepository;
import com.sparta.fitnus.user.entity.AuthUser;
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
    public List<FitnessResponse> getAllFitness() {
        return fitnessRepository.findAll().stream()
                .map(FitnessResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public FitnessResponse updateFitness(AuthUser authUser, Long fitnessId, FitnessRequest fitnessRequest) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new NotFoundException("해당 피트니스 아이디는 존재하지 않습니다.");
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
            throw new NotFoundException("해당 피트니스 아이디는 존재하지 않습니다.");
        }
        Center center = centerService.getCenterId(isValidCenterInFitness(fitnessId));
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }

        fitnessRepository.deleteById(fitnessId);


    }
    public Long isValidCenterInFitness(Long fitnessId) {
        return fitnessRepository.findCenterIdByFitnessId(fitnessId).orElseThrow(() ->
                new NotFoundException("해당 fitnessId는 없는 Id입니다."));
    }

    public Fitness isValidFitness(Long fitnessId) {
        return fitnessRepository.findById(fitnessId).orElseThrow(() ->
                new NotFoundException("Fitness not found"));
    }
}


