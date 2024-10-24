package com.sparta.fitnus.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L; // 60분 (테스트용)
    private final long REFRESH_TOKEN_EXPIRATION = 1 * 24 * 60 * 60 * 1000L; // 1일
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    // Secret Key를 기반으로 HMAC SHA256 알고리즘을 위한 초기화
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access Token 생성 (Bearer prefix 없이 쿠키에 저장)
    public String createAccessToken(Long userId, String email, String role, String nickname) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(Long.toString(userId))  // 사용자 ID 설정
                .claim("UserRole", role)  // 사용자 권한 추가
                .claim("email", email)  // 이메일 추가
                .claim("nickname", nickname)  // 닉네임 추가
                .setIssuedAt(now)  // 발급 시간
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))  // 만료 시간 설정
                .signWith(key, signatureAlgorithm)  // 서명 알고리즘과 키로 서명
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(Long.toString(userId))  // 사용자 ID 설정
                .setIssuedAt(now)  // 발급 시간
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION))  // 만료 시간 설정
                .signWith(key, signatureAlgorithm)  // 서명 알고리즘과 키로 서명
                .compact();
    }

    // Token 유효성 검사 (Access Token과 Refresh Token 둘 다 검증 가능)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;  // 토큰이 유효하면 true 반환
        } catch (ExpiredJwtException e) {
            throw e;  // 만료된 경우 예외를 던짐 (Refresh 용도로 따로 처리)
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    // JWT에서 클레임(Claims) 추출 (추가된 메서드)
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // 서명에 사용된 키로 파싱
                .build()
                .parseClaimsJws(token)  // 토큰에서 클레임 추출
                .getBody();  // 클레임 반환
    }

    // Access Token을 쿠키에 저장 (Bearer prefix 없이)
    public void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
        cookie.setHttpOnly(true);  // XSS 공격 방지를 위해 HttpOnly 설정
        cookie.setMaxAge(7 * 24 * 60 * 60);  // 쿠키의 만료 시간 7일로 설정 (Access Token 만료 시간과는 별개)
        cookie.setPath("/");  // 쿠키의 경로를 루트로 설정
        response.addCookie(cookie);
    }

    // 쿠키에서 Access Token 추출
    public String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    return cookie.getValue();  // 쿠키에서 JWT 토큰 값 반환
                }
            }
        }
        return null;  // 쿠키가 없으면 null 반환
    }

    // Bearer prefix를 제거하는 메서드
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);  // "Bearer " 제거 후 반환
        }
        throw new IllegalArgumentException("Token does not start with Bearer");
    }

    // 쿠키에서 Access Token 삭제
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, null);
        cookie.setMaxAge(0);  // 쿠키 즉시 만료
        cookie.setHttpOnly(true);
        cookie.setPath("/");  // 쿠키 경로 설정
        response.addCookie(cookie);
    }

}





