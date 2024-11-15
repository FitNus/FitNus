package com.sparta.service.center.service;

import com.sparta.common.dto.AuthUser;
import com.sparta.common.enums.UserRole;
import com.sparta.service.center.dto.request.CenterSaveRequest;
import com.sparta.service.center.dto.request.CenterUpdateRequest;
import com.sparta.service.center.dto.response.CenterResponse;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.entity.CenterSearch;
import com.sparta.service.center.exception.CenterAccessDeniedException;
import com.sparta.service.center.exception.CenterNotFoundException;
import com.sparta.service.center.repository.CenterCacheRepository;
import com.sparta.service.center.repository.CenterRepository;
import com.sparta.service.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CenterService {

    private final CenterRepository centerRepository;
    private final LocationService locationService;
    private final CenterCacheRepository cacheRepository;
    private final SearchService searchService;

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

        // 위도, 경도 정보가 null인 경우 처리 (예외 처리)
        if (latLng == null || latLng.latitude() == null || latLng.longitude() == null) {
            throw new IllegalArgumentException("주소에서 유효한 위도와 경도를 가져올 수 없습니다.");
        }

        Center center = Center.of(request, authUser, latLng.latitude(), latLng.longitude());

        Center savedCenter = centerRepository.save(center);

        try {
            // Redis에 센터 위치 정보 저장
            cacheRepository.saveGeoLocation("centers", latLng.longitude(), latLng.latitude(),
                    center.getId());
        } catch (Exception e) {
            // Redis 저장 실패 시 로깅
            log.error("Redis에 위치 정보를 저장하는 중 오류 발생: {}", e.getMessage());
        }

        // Elasticsearch에 센터 정보 저장
        searchService.saveCenterSearch(new CenterSearch(center));  // SearchService에서 호출

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
    public CenterResponse updateCenter(AuthUser authUser, Long centerId,
                                       CenterUpdateRequest updateRequest) {
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

    /**
     * 사용자 주위 센터 검색
     *
     * @param userLongitude
     * @param userLatitude
     * @param radius
     * @return
     */
    @Transactional
    public List<CenterResponse> findNearbyCenters(Double userLongitude, Double userLatitude,
                                                  Double radius) {
        //사용자의 위치에서 지정된 반경 내의 센터를 검색
        GeoResults<GeoLocation<String>> results = cacheRepository.findCentersWithinRadius(
                userLongitude, userLatitude, radius);

        //결과를 CenterResponse로 변환하여 반환
        List<CenterResponse> nearbyCenters = new ArrayList<>();
        for (GeoResult<GeoLocation<String>> result : results) {
            String centerId = result.getContent().getName();

            //데이터베이스에서 센터 ID로 센터 정보를 가져와 CenterResponse로 변환
            Center center = centerRepository.findById(Long.parseLong(centerId))
                    .orElse(null);

            if (center != null) {
                nearbyCenters.add(new CenterResponse(center));
            }
        }
        return nearbyCenters;
    }

    public Long isValidOwnerInCenter(Long centerId) {
        return centerRepository.findOwnerIdByCenterId(centerId)
                .orElseThrow(CenterNotFoundException::new);
    }

    // CenterService.java
    public Center getCenterId(Long id) {
        return centerRepository.findById(id)
                .orElseThrow(CenterNotFoundException::new);
    }

}