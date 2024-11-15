package com.sparta.service.center.repository;

import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;

public interface CenterCacheRepository {

    void saveGeoLocation(String key, Double longitude, Double latitude, Long id);

    GeoResults<RedisGeoCommands.GeoLocation<String>> findCentersWithinRadius(Double longitude, Double latitude, Double radius);
}
