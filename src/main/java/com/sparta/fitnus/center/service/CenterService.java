package com.sparta.fitnus.center.service;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
import com.sparta.fitnus.center.dto.response.CenterResponse;
import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.exception.CenterAccessDeniedException;
import com.sparta.fitnus.center.repository.CenterRepository;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterService {
    private final CenterRepository centerRepository;

    /***
     * CRUD-Get : getCenter()의 기능입니다.
     * @param centerId
     * @return centerRepository
     */
    public CenterResponse getCenter(Long centerId) {
        return centerRepository.findById(centerId)
                .map(CenterResponse::new)
                .orElseThrow(
                        () -> new NotFoundException("Center with id " + centerId + " not found")
                );
    }

    /***
     * CRUD-POST : saveCenter()의 기능입니다.
     * @param request
     * @return CenterResponse
     * 예외처리 : OWNER 권한 체크 - @Secured(UserRole.Authority.OWNER)
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public CenterResponse addCenter(AuthUser authUser, CenterSaveRequest request) {
        Center center = Center.of(request, authUser);
        Center savedCenter = centerRepository.save(center);
        return new CenterResponse(savedCenter);
    }


    /***
     * CRUD-PATCH : updateCenter()의 기능입니다.
     * @param authUser
     * @param centerId
     * @param updateRequest
     * @return
     * 예외처리1) : OWNER 권한 체크 - @Secured(UserRole.Authority.OWNER)
     * 예외처리2) : OWNER가 이 센터를 만든 OWNER랑 같은 ID인지 체크 - ownerId.equals(currentUserId)
     */
    @Transactional
    @Secured(UserRole.Authority.OWNER)
    public CenterResponse updateCenter(AuthUser authUser, Long centerId, CenterUpdateRequest updateRequest) {
        Long ownerId = isValidOwnerInCenter(centerId);
        Long currentUserId = authUser.getId(); // 현재 사용자 ID 가져오기

        if (!ownerId.equals(currentUserId)) {
            throw new CenterAccessDeniedException();
        }
        Center center = centerRepository.findCenterById(centerId);
        center.update(updateRequest);

        return new CenterResponse(center);
    }

    /***
     * CRUD-DELETE : deleteCenter()의 기능입니다.
     * @param centerId
     * 예외처리1) : OWNER 권한 체크 - @Secured(UserRole.Authority.OWNER)
     * 예외처리2) : OWNER가 이 센터를 만든 OWNER랑 같은 ID인지 체크 - ownerId.equals(currentUserId)
     */
    @Transactional
    @Secured(UserRole.Authority.OWNER)
    public void deleteCenter(AuthUser authUser, Long centerId) {
        Long ownerId = isValidOwnerInCenter(centerId);
        Long currentUserId = authUser.getId(); // 현재 사용자 ID 가져오기

        if (!ownerId.equals(currentUserId)) {
            throw new CenterAccessDeniedException();
        }
        centerRepository.deleteById(centerId);
    }

    public Long isValidOwnerInCenter(Long centerId) {
        return centerRepository.findOwnerIdByCenterId(centerId).orElseThrow(() ->
                new NotFoundException("해당 CenterId는 없는 Id입니다."));
    }

    // CenterService.java
    public Center getCenterId(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Center with id " + id + " not found"));
    }


}