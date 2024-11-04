package com.sparta.modulecommon.center.service;

import com.sparta.modulecommon.center.dto.request.CenterSaveRequest;
import com.sparta.modulecommon.center.dto.request.CenterUpdateRequest;
import com.sparta.modulecommon.center.dto.response.CenterResponse;
import com.sparta.modulecommon.center.entity.Center;
import com.sparta.modulecommon.center.exception.CenterAccessDeniedException;
import com.sparta.modulecommon.center.exception.CenterNotFoundException;
import com.sparta.modulecommon.center.repository.CenterRepository;
import com.sparta.modulecommon.user.entity.AuthUser;
import com.sparta.modulecommon.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterService {
    private final CenterRepository centerRepository;
    private final LocationService locationService;

    /***
     * CRUD-Get : getCenter()의 기능입니다.
     * @param centerId
     * @return centerRepository
     */
    public CenterResponse getCenter(Long centerId) {
        Center center = centerRepository.findCenterById(centerId);

        return new CenterResponse(center);
    }

    /***
     * CRUD-POST : saveCenter()의 기능입니다.
     * @param request
     * @return CenterResponse
     * 예외처리 : OWNER 권한 체크 - @Secured(UserRole.Authority.OWNER)
     */
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public CenterResponse createCenter(AuthUser authUser, CenterSaveRequest request) {
        // 주소로부터 위도와 경도 가져오기
        LocationService.LatLng latLng = locationService.getLatLngFromAddress(request.getAddress());

        Center center = Center.of(request, authUser, latLng.latitude(), latLng.longitude());
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
    @Secured(UserRole.Authority.OWNER)
    @Transactional
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
    @Secured(UserRole.Authority.OWNER)
    @Transactional
    public void deleteCenter(AuthUser authUser, Long centerId) {
        Long ownerId = isValidOwnerInCenter(centerId);
        Long currentUserId = authUser.getId(); // 현재 사용자 ID 가져오기

        if (!ownerId.equals(currentUserId)) {
            throw new CenterAccessDeniedException();
        }
        centerRepository.deleteById(centerId);
    }

    public Long isValidOwnerInCenter(Long centerId) {
        return centerRepository.findOwnerIdByCenterId(centerId).orElseThrow(CenterNotFoundException::new);
    }

    // CenterService.java
    public Center getCenterId(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(CenterNotFoundException::new);
    }


}