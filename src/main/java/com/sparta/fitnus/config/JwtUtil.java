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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "refreshToken";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long ACCESS_TOKEN_EXPIRATION = 60 * 60 * 1000L; // 60분
    private final long REFRESH_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000L; // 1일
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
        return BEARER_PREFIX + Jwts.builder()
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
        return BEARER_PREFIX + Jwts.builder()
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
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setHttpOnly(true);  // XSS 공격 방지를 위해 HttpOnly 설정. HttpOnly로 설정하여 JavaScript에서 접근 불가
            cookie.setMaxAge(24 * 60 * 60);  // Access Token 쿠키의 만료 시간 1일로 설정 (Access Token 만료 시간과는 별개)
            cookie.setPath("/");  // 쿠키의 경로를 루트로 설정
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Refresh Token 쿠키 설정
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        try {
            refreshToken = URLEncoder.encode(refreshToken, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행
            Cookie cookie = new Cookie(REFRESH_TOKEN_HEADER, refreshToken);
            cookie.setHttpOnly(true);        // XSS 공격 방지를 위해 HttpOnly 설정. HttpOnly로 설정하여 JavaScript에서 접근 불가
            cookie.setMaxAge(1 * 24 * 60 * 60);  // Refresh Token 쿠키의 만료 시간 1일로 설정 (Refresh Token 만료 시간과는 별개)
            cookie.setPath("/");             // 쿠키의 경로를 루트로 설정
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 쿠키에서 Access Token 추출
    public String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(AUTHORIZATION_HEADER)) {
                    try {
                        // 쿠키 값 디코딩
                        return java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("Error decoding token from cookie", e);
                    }
                }
            }
        }
        return null;  // 쿠키가 없으면 null 반환
    }

    // Bearer prefix 제거
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue)) {
            if (tokenValue.startsWith(BEARER_PREFIX)) {
                return tokenValue.substring(BEARER_PREFIX.length());
            }
            return tokenValue;  // prefix가 없는 경우에도 예외 없이 그대로 반환
        }
        throw new NullPointerException("Not Found Token");
    }


    public void clearAllCookies(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 모든 쿠키가져옴.
        Cookie[] cookies = request.getCookies();

        // 모든 쿠키에 대해 값과 만료를 설정하여 삭제.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue(null);
                cookie.setMaxAge(0);  // 즉시 만료
                cookie.setHttpOnly(true);
                cookie.setPath("/");  // 모든 경로에 대해 삭제 적용
                response.addCookie(cookie);
            }
        }
    }
}





