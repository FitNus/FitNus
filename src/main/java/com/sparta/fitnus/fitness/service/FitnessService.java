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
     * CRUD-POST : addFitness()의 기능입니다.
     * @param authUser
     * @param request
     * @return FitnessResponse
     */
    @Transactional
    public FitnessResponse addFitness(Long fitnessId, AuthUser authUser, FitnessRequest request) {

        Long ownerId = isValidCenterInFitness(fitnessId);
        Long currentUserId = authUser.getId();
        if (!currentUserId.equals(ownerId)) {
            throw new AccessDeniedException();
        }
        Center center = centerService.getCenterId(ownerId);
        Fitness fitness = Fitness.of(request, center);
        Fitness savedfitness = fitnessRepository.save(fitness);
        return new FitnessResponse(savedfitness);
    }


    /***
     * CRUD-GET(단건조회) : getFitness()의 기능입니다.
     * @param id
     * @return FitnessResponse
     */
    public FitnessResponse getFitness(Long id) {
        return fitnessRepository.findById(id)
                .map(FitnessResponse::new)
                .orElseThrow(FitnessNotFoundException::new);
    }

    /***
     * CRUD-GET(다건조회) : getAllFitness()의 기능입니다.
     * @return List<FitnessResponse>
     */
    public List<FitnessResponse> getAllFitness() {
        return fitnessRepository.findAll().stream()
                .map(FitnessResponse::new)
                .collect(Collectors.toList());
    }

    /***
     * CRUD-PATCH : updateFitness()의 기능입니다.
     * @param authUser
     * @param fitnessId
     * @param fitnessRequest
     * @return FitnessResponse
     */
    @Transactional
    public FitnessResponse updateFitness(AuthUser authUser, Long fitnessId,
            FitnessRequest fitnessRequest) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new FitnessNotFoundException();
        }
        Long ownerId = isValidCenterInFitness(fitnessId);
        Long currentUserId = authUser.getId();
        if (!currentUserId.equals(ownerId)) {
            throw new AccessDeniedException();
        }
        Fitness fitness = isValidFitness(fitnessId);
        fitness.update(fitnessRequest);
        return new FitnessResponse(fitness);
    }


    /***
     * CRUD-DELETE : deleteFitness()의 기능입니다.
     * @param authUser
     * @param fitnessId
     */
    @Transactional
    public void deleteFitness(AuthUser authUser, Long fitnessId) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new FitnessNotFoundException();
        }
        Long ownerId = isValidCenterInFitness(fitnessId);
        Long currentUserId = authUser.getId();
        if (!currentUserId.equals(ownerId)) {
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


