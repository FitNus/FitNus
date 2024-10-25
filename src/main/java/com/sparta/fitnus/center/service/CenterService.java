package com.sparta.fitnus.center.service;

import com.sparta.fitnus.center.dto.request.CenterSaveRequest;
import com.sparta.fitnus.center.dto.request.CenterUpdateRequest;
import com.sparta.fitnus.center.dto.response.CenterResponse;
import com.sparta.fitnus.center.entity.Center;
import com.sparta.fitnus.center.repository.CenterRepository;
import com.sparta.fitnus.common.exception.ForbiddenException;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterService {
    private final CenterRepository centerRepository;

    /***
     * CRUD-POST : getCenter()의 기능입니다.
     * @param centerId
     * @return
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
     * @return
     */
    @Transactional
    public CenterResponse addCenter(CenterSaveRequest request) {
        Center center = Center.of(request);
        Center savedCenter = centerRepository.save(center);
        return new CenterResponse(savedCenter);
    }

    /***
     * CRUD-PATCH : updateCenter()의 기능입니다.
     * @param authUser
     * @param centerId
     * @param updateRequest
     * @return
     */
    @Transactional
    public CenterResponse updateCenter(AuthUser authUser, Long centerId, CenterUpdateRequest updateRequest) {
        // 권한확인 == OWNER가 아닌경우, 바로 Exception 던지고 종료.
//        if (!authUser.getAuthorities().equals(UserRole.OWNER)) {
//            throw new ForbiddenException("센터를 수정할 권한이 없습니다.");
//        }
        Center center = centerRepository.findCenterById(centerId);
        center.update(updateRequest);

        return new CenterResponse(center);
    }

    /***
     * CRUD-DELETE : deleteCenter()의 기능입니다.
     * @param authuser
     * @param centerId
     */
    @Transactional
    public void deleteCenter(AuthUser authuser, Long centerId) {
        if (!authuser.getAuthorities().stream().findFirst().get().equals(UserRole.OWNER)) {
            centerRepository.deleteById(centerId);
        } else {
            throw new ForbiddenException("이 센터를 삭제할 권한이 없습니다.");
        }


    }

    // CenterService.java
    public Center getCenterId(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Center with id " + id + " not found"));
    }

}