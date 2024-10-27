package com.sparta.fitnus.user.service;

import com.sparta.fitnus.common.exception.*;
import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.user.dto.request.ChangePasswordRequest;
import com.sparta.fitnus.user.dto.request.UserRequest;
import com.sparta.fitnus.user.dto.response.UserResponse;
import com.sparta.fitnus.user.entity.AuthUser;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.enums.UserStatus;
import com.sparta.fitnus.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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
        //비밀번호검증
        validatePassword(userRequest.getPassword(), user.getPassword());
        //유저 상태 검증
        validateStatus(user.getStatus());
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

    @Transactional
    public String changePassword(AuthUser authUser, Long userId, ChangePasswordRequest request) {
        User user = getUser(userId);
        //로그인한 유저와 비밀번호 교체 유저 일치 검증
        validateUser(authUser, userId);
        //비밀번호 일치 검증
        validatePassword(request.getOldPassword(), user.getPassword());

        //비밀번호 변경
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedPassword);
        //Db에 저장
        userRepository.save(user);
        return "비밀번호 변경 완료";
    }

    @Transactional
    public String deleteUser(AuthUser authUser, Long userId, UserRequest userRequest, HttpServletResponse response) {
        User user = getUser(userId);
        String password = userRequest.getPassword();
        //로그인한 유저와 탈퇴 시도 유저 일치 검증
        validateUser(authUser, userId);
        //비밀번호 일치 검증
        validatePassword(password, user.getPassword());
        //Db에서 탈퇴
        userRepository.delete(user);
        // Redis에서 Refresh Token 삭제
        redisUserService.deleteTokens(authUser.getId().toString());

        // 쿠키에서 Access Token 삭제º
        jwtUtil.clearTokenCookie(response);
        return "탈퇴 완료";
    }

    @Transactional
    public String deactivateUser(Long userId, AuthUser authUser) {
        User user = getUser(userId);
        //admin권한 검증
        Collection<? extends GrantedAuthority> authorities = authUser.getAuthorities();
        validateAdmin(authorities);
        //유저 status 검증
        validateStatus(user.getStatus());
        //유저 deactivate
        user.deactivate();
        // Redis에 해당 유저의 토큰이 남아있다면 Refresh Token 삭제
        redisUserService.deleteTokens(userId.toString());
        //Db에 저장
        userRepository.save(user);
        return "유저 비활성화 완료";
    }

    private void validateStatus(UserStatus status) {
        if (status.equals(UserStatus.BANNED)) {
            throw new UserBannedException();
        }
    }

    private void validateAdmin(Collection<? extends GrantedAuthority> authorities) {
        if (!authorities.contains(new SimpleGrantedAuthority("ADMIN"))) {
            throw new NotAdminException();
        }
    }

    private void validateUser(AuthUser authUser, Long userId) {
        if (!userId.equals(authUser.getId())) {
            throw new WrongUserException();
        }
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

