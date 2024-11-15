package com.sparta.user.kakao.service;

import com.sparta.common.enums.UserRole;
import com.sparta.user.config.JwtUtil;
import com.sparta.user.kakao.exception.KakaoAccountException;
import com.sparta.user.kakao.exception.KakaoApiException;
import com.sparta.user.kakao.exception.KakaoEmailException;
import com.sparta.user.user.dto.response.AuthTokenResponse;
import com.sparta.user.user.entity.User;
import com.sparta.user.user.repository.UserRepository;
import com.sparta.user.user.service.RedisUserService;
import com.sparta.user.user.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUserService redisUserService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;
    @Value("${kakao.client.redirect}")
    private String kakaoClientRedirect;
    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;

    // 카카오 인증 처리
    public AuthTokenResponse handleKakaoAuth(String code) {
        String accessToken = getAccessToken(code);
        String email = getEmailFromKakao(accessToken);
        User user = findOrCreateUser(email);

        String newAccessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        // Redis에 토큰 저장
        redisUserService.saveTokens(String.valueOf(user.getId()), newAccessToken, refreshToken);

        // 결과 반환
        return new AuthTokenResponse(newAccessToken, refreshToken);
    }

    // Access Token 요청
    public String getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoClientRedirect +
                "&code=" + code +
                "&client_secret=" + kakaoClientSecret;

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new KakaoApiException("Failed to retrieve access token from Kakao");
        }

        return responseBody.get("access_token").toString();
    }

    // 사용자 이메일 정보 요청
    public String getEmailFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://kapi.kakao.com/v2/user/me";

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return extractEmail(response);

        } catch (Exception e) {
            throw new KakaoApiException("Failed to retrieve user information from Kakao");
        }
    }

    // 응답에서 이메일 추출
    private static @NotNull String extractEmail(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("kakao_account")) {
            throw new KakaoAccountException();
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) responseBody.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        if (email == null) {
            throw new KakaoEmailException();
        }
        return email;
    }

    // 이메일로 사용자 조회 또는 신규 생성
    public User findOrCreateUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            return registerKakaoUser(email);
        }
    }

    // 새로운 카카오 사용자 등록
    private User registerKakaoUser(String email) {
        userService.validateDuplicateEmail(email);
        String randomPassword = generateRandomPassword();
        String randomNickname = generateRandomNickname();
        String encodedPassword = passwordEncoder.encode(randomPassword);

        User newUser = User.of(email, encodedPassword, randomNickname, UserRole.USER);
        return userRepository.save(newUser);
    }

    // 랜덤 비밀번호 생성
    private String generateRandomPassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialCharacters = "!@#$";

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        String allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;
        for (int i = 4; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars);

        StringBuilder shuffledPassword = new StringBuilder();
        for (char c : passwordChars) {
            shuffledPassword.append(c);
        }

        return shuffledPassword.toString();
    }

    // 랜덤 닉네임 생성
    private String generateRandomNickname() {
        return "User_" + UUID.randomUUID().toString().substring(0, 6);
    }

    // JWT Access Token 생성
    public String createAccessToken(User user) {
        Long userId = user.getId();
        String role = user.getUserRole().name();
        String nickname = user.getNickname();
        return jwtUtil.createAccessToken(userId, user.getEmail(), role, nickname);
    }

    // JWT Refresh Token 생성
    public String createRefreshToken(User user) {
        Long userId = user.getId();
        return jwtUtil.createRefreshToken(userId);
    }
}
