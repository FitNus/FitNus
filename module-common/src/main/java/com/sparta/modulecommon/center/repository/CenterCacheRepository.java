package com.sparta.modulecommon.center.repository;

import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;

public interface CenterCacheRepository {

    void saveGeoLocation(String key, double longitude, double latitude, Long id);

    GeoResults<RedisGeoCommands.GeoLocation<String>> findCentersWithinRadius(double longitude, double latitude, double radius);
}