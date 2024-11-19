package com.sparta.service.center.repository;

import com.sparta.service.center.service.LocationService;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;

public interface CenterCacheRepository {

    // GeoSpatial 기능으로 센터 위치 저장
    void saveGeoLocation(String key, Double longitude, Double latitude, Long id);

    // 반경 내 센터 검색
    GeoResults<RedisGeoCommands.GeoLocation<String>> findCentersWithinRadius(Double longitude, Double latitude, Double radius);

    // 주소 -> 위도/경도 변환 결과 캐싱
    void cacheLocation(String address, LocationService.LatLng latLng, long ttlSeconds);

    // 캐싱된 위도/경도 정보 조회
    LocationService.LatLng getCachedLocation(String address);

}
