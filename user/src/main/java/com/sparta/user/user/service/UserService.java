package com.sparta.user.user.service;

import com.sparta.common.config.JwtUtil;
import com.sparta.common.config.RedisUserService;
import com.sparta.common.enums.UserRole;
import com.sparta.common.enums.UserStatus;
import com.sparta.common.exception.NotFoundException;
import com.sparta.common.user.dto.AuthUser;
import com.sparta.common.user.dto.UserRequest;
import com.sparta.common.user.entity.User;
import com.sparta.common.user.repository.UserRepository;
import com.sparta.user.user.dto.request.ChangePasswordRequest;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import com.sparta.user.user.dto.response.UserResponse;
import com.sparta.user.user.exception.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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
    private final EmailService emailService;

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

    // 비밀번호 재설정 요청
    @Transactional
    public String requestPasswordReset(String email) {
        User user = getUserFromEmail(email);

        // 랜덤 코드 생성
        String resetCode = generateResetCode();

        // Redis에 저장 (5분 TTL)
        redisUserService.saveCode(email, resetCode);

        // 이메일 전송
        String emailContent = "<p>안녕하세요,</p>"
                + "<p>아래의 코드를 사용하여 비밀번호를 재설정하세요:</p>"
                + "<p><b>" + resetCode + "</b></p>";

        try {
            emailService.sendEmail(user.getEmail(), "비밀번호 재설정 코드", emailContent);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }

        return "비밀번호 재설정 코드가 이메일로 전송되었습니다.";
    }

    // 비밀번호 변경
    @Transactional
    public String resetPassword(String email, String code, String newPassword) {
        // Redis에서 코드 검증
        String savedCode = redisUserService.getCode(email);
        if (savedCode == null || !savedCode.equals(code)) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 코드입니다.");
        }

        // 사용자 찾기
        User user = getUserFromEmail(email);

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 사용된 코드 삭제
        redisUserService.deleteCode(email);

        return "비밀번호가 성공적으로 변경되었습니다.";
    }

    // 랜덤 코드 생성
    private String generateResetCode() {
        return RandomStringUtils.randomNumeric(6); // 6자리 숫자
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

