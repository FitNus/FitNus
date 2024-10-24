package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.exception.DuplicateEmailException;
import com.sparta.fitnus.common.exception.NotFoundException;
import com.sparta.fitnus.common.exception.WrongAdminTokenException;
import com.sparta.fitnus.common.exception.WrongPasswordException;
import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    @Value("${admin.token}")
    private String ADMIN_TOKEN;
    @Value("${owner.token}")
    private String OWNER_TOKEN;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUserService redisUserService;

    @Transactional
    public UserResponse signup(@Valid UserRequest userRequest) {
        //이메일 중복검증
        validateDuplicateEmail(userRequest.getEmail());
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setEncodedPassword(encodedPassword);
        UserRole role = UserRole.USER;
        //adminToken 검증
        role = validateAdminToken(userRequest, role);

        //OwnerToken 검증
        role = validateOwnerToken(userRequest, role);

        User user = User.of(userRequest, role);
        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }

    public String login(UserRequest userRequest, HttpServletResponse response) {
        User user = getUserFromEmail(userRequest.getEmail());
        validatePassword(userRequest.getPassword(), user.getPassword());

        // ACCESS_TOKEN와 REFRESH_TOKEN 생성
        Long userId = user.getId();  // 사용자 id
        String role = user.getUserRole().name();  // 역할
        String nickname = user.getNickname();


        // Access Token과 Refresh Token 발급
        String accessToken = jwtUtil.createAccessToken(userId, user.getEmail(), role, nickname);
        String refreshToken = jwtUtil.createRefreshToken(userId);

        // Redis에 토큰 저장 (Access Token과 Refresh Token)
        redisUserService.saveTokens(String.valueOf(userId), accessToken, refreshToken);

        // Access Token을 쿠키에 저장
        jwtUtil.setTokenCookie(response, accessToken);

        // Refresh Token을 응답 헤더에 담아 보내는 방법 (예시)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "refreshToken=" + refreshToken + "; HttpOnly; Path=/; Max-Age=604800");

        return "로그인 완료";
    }

    public String logout(AuthUser authUser, HttpServletResponse response) {
        // Redis에서 Refresh Token 삭제
        redisUserService.deleteTokens(authUser.getId().toString());

        // 쿠키에서 Access Token 삭제º
        jwtUtil.clearTokenCookie(response);
        return "로그아웃 완료";
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new WrongPasswordException();
        }
    }

    public User getUser(Long userId) {
        return userRepository.findUserById(userId);
    }

    public User getUserFromEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email " + email + " not found")
        );
    }

    private UserRole validateOwnerToken(UserRequest userRequest, UserRole role) {
        if (userRequest.isOwner()) {
            if (!OWNER_TOKEN.equals(userRequest.getOwnerToken())) {
                throw new WrongAdminTokenException();
            }
            role = UserRole.OWNER;
        }
        return role;
    }


    public UserRole validateAdminToken(UserRequest userRequest, UserRole role) {
        if (userRequest.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRequest.getAdminToken())) {
                throw new WrongAdminTokenException();
            }
            role = UserRole.ADMIN;
        }
        return role;
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException();
        }
    }
}

