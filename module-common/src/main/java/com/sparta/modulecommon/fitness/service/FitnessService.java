package com.sparta.modulecommon.fitness.service;

import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.center.entity.CenterSearch;
import com.sparta.modulecommon.center.service.CenterService;
import com.sparta.modulecommon.fitness.dto.request.FitnessDeleteRequest;
import com.sparta.modulecommon.fitness.dto.request.FitnessRequest;
import com.sparta.modulecommon.fitness.dto.response.FitnessResponse;
import com.sparta.modulecommon.fitness.entity.Fitness;
import com.sparta.modulecommon.fitness.exception.AccessDeniedException;
import com.sparta.modulecommon.fitness.exception.FitnessNotFoundException;
import com.sparta.modulecommon.fitness.exception.FitnessgetAllAccessDeniedException;
import com.sparta.modulecommon.fitness.repository.FitnessRepository;
import com.sparta.modulecommon.search.service.SearchService;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.enums.UserRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FitnessService {

    private final FitnessRepository fitnessRepository;
    private final CenterService centerService;
    private final SearchService searchService;

    /***
     * CRUD-POST : createFitness()의 기능입니다.
     * @param authUser
     * @param request
     * @return FitnessResponse
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public FitnessResponse createFitness(AuthUser authUser, FitnessRequest request) {
        Center center = centerService.getCenterId(request.getCenterId());
        if (!authUser.getId().equals(centerService.getCenterId(request.getCenterId()).getId())) {
            throw new FitnessgetAllAccessDeniedException();
        }
        Fitness fitness = Fitness.of(request, center);
        Fitness savedfitness = fitnessRepository.save(fitness);
        searchService.saveFitnessName(new CenterSearch(center));
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
     * 상세설명 : 해당 센터를 만든 센터장이, 자신이 등록한 운동종목을 전부 확인하는 메서드입니다.
     * 예외처리 1) : 현재 로그인한 유저가, 센터를 만든 센터장인지 확인
     * @return List<FitnessResponse>
     */
    @Secured(UserRole.Authority.OWNER)
    public List<FitnessResponse> getAllFitness(AuthUser authuser, Long id) {
        // 예외처리 1)
        if (!authuser.getId().equals(centerService.getCenterId(id).getId())) {
            throw new FitnessgetAllAccessDeniedException();
        }
        return fitnessRepository.findAllByCenterId(id).stream()
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
    @Secured(UserRole.Authority.OWNER)
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
     * CRUD-DELETE : deleteFitness()의 기능입니다.
     * @param authUser
     * @param fitnessId
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public void deleteFitness(AuthUser authUser, Long fitnessId, FitnessDeleteRequest request) {
        if (fitnessRepository.findById(fitnessId).isEmpty()) {
            throw new FitnessNotFoundException();
        }
        Center center = centerService.getCenterId(request.getCenterId());
        if (!authUser.getId().equals(center.getOwnerId())) {
            throw new AccessDeniedException();
        }
        fitnessRepository.deleteById(fitnessId);
    }

    public Fitness isValidFitness(Long fitnessId) {
        return fitnessRepository.findById(fitnessId).orElseThrow(FitnessNotFoundException::new);
    }
}


