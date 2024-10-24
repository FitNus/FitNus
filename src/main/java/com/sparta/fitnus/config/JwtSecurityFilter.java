package com.sparta.fitnus.config;

import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.service.RedisUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUserService redisUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 쿠키에서 Access Token 추출
        String accessToken = jwtUtil.resolveTokenFromCookie(request);

        if (accessToken != null) {
            try {
                // Access Token 유효성 검사
                if (jwtUtil.validateToken(accessToken)) {
                    log.info("Access token is valid, continuing request");

                    // Access Token에서 사용자 정보 및 권한 추출
                    Claims claims = jwtUtil.extractClaims(accessToken);
                    Long userId = Long.parseLong(claims.getSubject());
                    String userRole = claims.get("UserRole", String.class);
                    String email = claims.get("email", String.class);

                    // AuthUser 객체 생성
                    AuthUser authUser = new AuthUser(userId, UserRole.valueOf(userRole), email);

                    // SecurityContext에 AuthUser 설정
                    Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // 필터 체인 계속 진행
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (ExpiredJwtException e) {
                log.info("Access token expired, attempting to refresh using Refresh Token");

                // 만료된 Access Token에서 클레임 추출
                Claims claims = e.getClaims();
                Long userId = Long.parseLong(claims.getSubject());
                String email = claims.get("email", String.class);
                String userRole = claims.get("UserRole", String.class);

                // Redis에서 Refresh Token 조회
                String refreshToken = redisUserService.getRefreshToken(userId.toString());

                // Refresh Token 유효성 검사
                if (refreshToken != null && jwtUtil.validateToken(refreshToken)) {
                    log.info("Refresh token is valid. Issuing new access token.");

                    // 새로운 Access Token 생성
                    String newAccessToken = jwtUtil.createAccessToken(userId, email, userRole);

                    // Redis에 새로운 Access Token 저장 (필요시)
                    redisUserService.updateAccessToken(userId.toString(), newAccessToken);

                    // 새로운 Access Token을 쿠키에 저장
                    jwtUtil.setTokenCookie(response, newAccessToken);

                    // AuthUser 객체 생성 및 SecurityContext에 설정
                    AuthUser authUser = new AuthUser(userId, UserRole.valueOf(userRole), email);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("New access token issued and added to cookies. Proceeding with the request.");

                    // 필터 체인 계속 진행
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    log.warn("Refresh token expired or not found. Forcing re-login.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token expired. Please log in again.");
                    return;
                }
            }
        } else {
            log.warn("No access token found in request. Possibly trying to access a public endpoint.");
        }

        // Access Token이 없는 경우, 필터 체인을 계속 진행
        filterChain.doFilter(request, response);
    }
}







