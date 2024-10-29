package com.sparta.fitnus.kakao.service;

import com.sparta.fitnus.config.JwtUtil;
import com.sparta.fitnus.kakao.exception.KakaoAccountException;
import com.sparta.fitnus.kakao.exception.KakaoApiException;
import com.sparta.fitnus.kakao.exception.KakaoEmailException;
import com.sparta.fitnus.user.entity.User;
import com.sparta.fitnus.user.enums.UserRole;
import com.sparta.fitnus.user.repository.UserRepository;
import com.sparta.fitnus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

    @Value("${kakao.client.id}")
    private String kakaoClientId;
    @Value("${kakao.client.redirect}")
    private String kakaoClientRedirect;
    @Value("${kakao.client.secret}")
    private String kakaoClientSecret;

    public String getAccessToken(String code) {
        // RestTemplate 인스턴스를 생성
        RestTemplate restTemplate = new RestTemplate();

        // HttpHeaders 객체를 생성 및  HTTP 요청 헤더 설정.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 요청의 Content-Type 설정.

        String body = "grant_type=authorization_code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoClientRedirect +
                "&code=" + code +
                "&client_secret=" + kakaoClientSecret;

        // HttpEntity 객체에 헤더와 바디를 설정하여 요청 객체를 생성
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Access Token을 얻기 위한 카카오 OAuth2 토큰 발급 URL
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        // 카카오 API에 POST 요청을 보내고, 응답 Map 형식으로.
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        // 응답 Map에서 access_token 값 추출.
        Map<String, Object> responseBody = response.getBody();
        String accessToken = responseBody.get("access_token").toString();

        return accessToken;
    }

    public String getEmailFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보낼 RestTemplate 인스턴스 생성
        HttpHeaders headers = new HttpHeaders(); // HTTP 요청 헤더 생성
        headers.setBearerAuth(accessToken); // Authorization 헤더에 Bearer 형식 accessToken 설정

        HttpEntity<String> entity = new HttpEntity<>(headers); // 헤더를 포함한 HttpEntity 생성

        String url = "https://kapi.kakao.com/v2/user/me"; // 카카오 사용자 정보 API URL

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            //응답에서 카카오 계정 정보 추출
            String email = getEmail(response);

            return email;

        } catch (Exception e) {
            // 예외 발생 시 오류 메시지 출력 및 예외 전환
            throw new KakaoApiException(e.getMessage());
        }
    }

    //카카오 api응답에서 email추출
    private static @NotNull String getEmail(ResponseEntity<Map> response) {
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

    // 사용자 등록
    public User registerKakaoUser(String email) {
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

        // 각 유형에서 한 문자씩 추가
        password.append(upperCaseLetters.charAt(random.nextInt(upperCaseLetters.length())));
        password.append(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialCharacters.charAt(random.nextInt(specialCharacters.length())));

        // 나머지 4자리 무작위로 채우기
        String allCharacters = upperCaseLetters + lowerCaseLetters + digits + specialCharacters;
        for (int i = 4; i < 8; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // 비밀번호 셔플하여 순서 랜덤화
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars);

        // 셔플된 비밀번호 조합하여 반환
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

    public String createAccessToken(User user) {
        Long userId = user.getId();
        String role = user.getUserRole().name();
        String nickname = user.getNickname();
        //Access Token생성
        String accessToken = jwtUtil.createAccessToken(userId, user.getEmail(), role, nickname);
        return accessToken;
    }

    public String createRefreshToken(User user) {
        Long userId = user.getId();
        //Refresh Token생성
        String refreshToken = jwtUtil.createRefreshToken(userId);
        return refreshToken;
    }
}
