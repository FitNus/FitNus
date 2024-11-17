package com.sparta.service.center.repository;

import com.sparta.service.center.service.LocationService.LatLng;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CenterCacheRepositoryImpl implements CenterCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final GeoOperations<String, String> geoOperations;
    private static final String GEO_KEY = "centers";
    private static final String LOCATION_CACHE_KEY_PREFIX = "location:";

    @Override
    public void saveGeoLocation(String key, Double longitude, Double latitude, Long id) {
        geoOperations.add(key, new Point(longitude, latitude), id.toString());
    }

    @Override
    public GeoResults<GeoLocation<String>> findCentersWithinRadius(Double longitude, Double latitude, Double radius) {
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(radius, Metrics.KILOMETERS));
        return geoOperations.radius(GEO_KEY, circle);
    }

    @Override
    public void cacheLocation(String address, LatLng latLng, long ttlSeconds) {
        String cacheKey = LOCATION_CACHE_KEY_PREFIX + address;
        redisTemplate.opsForValue().set(cacheKey, latLng, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public LatLng getCachedLocation(String address) {
        String cacheKey = LOCATION_CACHE_KEY_PREFIX + address;
        return (LatLng) redisTemplate.opsForValue().get(cacheKey);
    }
}

