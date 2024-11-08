package com.sparta.modulecommon.center.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CenterRedisRepository implements CenterCacheRepository{

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveGeoLocation(String key, double longitude, double latitude, Long id) {
        redisTemplate.opsForGeo().add(
            "centers",  //redis 키
            new Point(longitude, latitude), //경도, 위도
            id.toString() //센터 ID
        );
    }

    @Override
    public GeoResults<GeoLocation<String>> findCentersWithinRadius(double longitude, double latitude, double radius) {
        // 사용자의 위치에서 지정된 반경 내의 센터 검색
        return redisTemplate.opsForGeo()
            .radius("centers",
                new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS))
            );
    }
}
