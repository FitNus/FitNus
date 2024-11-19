package com.sparta.service.center.controller;

import com.sparta.service.center.dto.response.CenterResponse;
import com.sparta.service.center.entity.Center;
import com.sparta.service.center.repository.CenterRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/centers")
@RequiredArgsConstructor
public class CenterUncacheController {

    private final CenterRepository centerRepository;

    /**
     * 캐싱하지 않고 데이터베이스에서 직접 반경 내 센터의 상세 정보 조회
     */
    @GetMapping("/search/uncached")
    public ResponseEntity<List<CenterResponse>> findNearbyCentersWithoutCaching(
        @RequestParam Double longitude,
        @RequestParam Double latitude,
        @RequestParam Double radius) {

        // 데이터베이스에서 모든 센터 조회
        List<Center> allCenters = centerRepository.findAll();
        List<CenterResponse> nearbyCenters = new ArrayList<>();

        // 거리 계산을 통해 반경 내 센터 필터링
        for (Center center : allCenters) {
            double distance = calculateDistance(latitude, longitude, center.getLatitude(), center.getLongitude());
            if (distance <= radius) {
                nearbyCenters.add(new CenterResponse(center));
            }
        }

        return ResponseEntity.ok(nearbyCenters);
    }

    /**
     * 두 지점 간의 거리를 계산하는 메서드 (Haversine formula)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // km 단위 거리 반환
    }
}