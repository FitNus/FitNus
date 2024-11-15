package com.sparta.user.user.service;

import com.sparta.common.dto.AuthUser;
import com.sparta.common.enums.UserRole;
import com.sparta.common.enums.UserStatus;
import com.sparta.common.exception.NotFoundException;
import com.sparta.user.config.JwtUtil;
import com.sparta.user.user.dto.request.ChangePasswordRequest;
import com.sparta.user.user.dto.request.UserRequest;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import com.sparta.user.user.dto.response.UserResponse;
import com.sparta.user.user.entity.User;
import com.sparta.user.user.exception.*;
import com.sparta.user.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    public void redisSaveTokens(Long userId, String accessToken, String refreshToken) {
        // Redis에 토큰 저장 (Access Token과 Refresh Token)
        redisUserService.saveTokens(String.valueOf(userId), accessToken, refreshToken);
    }

    public String createAccessToken(User user) {
        Long userId = user.getId();  // 사용자 id
        String role = user.getUserRole().name();  // 역할
        String nickname = user.getNickname();
        String email = user.getEmail();
        String accessToken = jwtUtil.createAccessToken(userId, email, role, nickname);
        return accessToken;
    }

    public String createRefreshToken(User user) {
        Long userId = user.getId();
        String refreshToken = jwtUtil.createRefreshToken(userId);
        return refreshToken;
    }

    public void deleteRedisToken(AuthUser authUser) {
        // Redis에서 Refresh Token 삭제
        redisUserService.deleteTokens(authUser.getId().toString());

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
    public void deleteUser(AuthUser authUser, Long userId, UserRequest userRequest) {
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
    }

    public User checkLogin(UserRequest userRequest) {
        User user = getUserFromEmail(userRequest.getEmail());
        //비밀번호검증
        validatePassword(userRequest.getPassword(), user.getPassword());
        //유저 상태 검증
        validateStatus(user.getStatus());
        return user;
    }


    @Transactional
    public String deactivateUser(Long userId, AuthUser authUser) {
        User user = getUser(userId);
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
                throw new WrongOwnerTokenException();
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

    public AuthTokenResponse login(UserRequest userRequest) {
        User user = checkLogin(userRequest);
        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);
        redisUserService.saveTokens(String.valueOf(user.getId()), accessToken, refreshToken);
        return new AuthTokenResponse(accessToken, refreshToken);
    }
}
