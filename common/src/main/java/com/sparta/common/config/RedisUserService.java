package com.sparta.common.config;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisUserService {

    private final StringRedisTemplate redisTemplate;

    public RedisUserService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Access Token과 Refresh Token 저장
    public void saveTokens(String userId, String accessToken, String refreshToken) {
        redisTemplate.opsForValue().set("ACCESS_TOKEN_" + userId, accessToken, 60, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("REFRESH_TOKEN_" + userId, refreshToken, 1, TimeUnit.DAYS);
    }

    // Refresh Token 조회
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("REFRESH_TOKEN_" + userId);
    }

    // Access Token 갱신
    public void updateAccessToken(String userId, String newAccessToken) {
        redisTemplate.opsForValue().set("ACCESS_TOKEN_" + userId, newAccessToken, 60, TimeUnit.MINUTES);
    }

    // 로그아웃 시 토큰 삭제
    public void deleteTokens(String userId) {
        redisTemplate.delete("ACCESS_TOKEN_" + userId);
        redisTemplate.delete("REFRESH_TOKEN_" + userId);
    }

}
