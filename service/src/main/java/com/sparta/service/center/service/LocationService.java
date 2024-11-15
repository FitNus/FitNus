package com.sparta.service.center.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestTemplate restTemplate;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public LatLng getLatLngFromAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET,
                entity, (Class<Map<String, Object>>) (Class<?>) Map.class);

        Map<String, Object> responseBody = response.getBody();

        // Kakao API 응답에서 "documents"가 존재하는지, 그 안에 위도/경도가 있는지 확인
        if (responseBody != null && !((List<?>) responseBody.get("documents")).isEmpty()) {
            List<Map<String, Object>> documents = (List<Map<String, Object>>) responseBody.get(
                    "documents");
            Map<String, Object> location = documents.get(0);
            Map<String, Object> addressInfo = (Map<String, Object>) location.get("address");

            String latitudeStr = (String) addressInfo.get("y");
            String longitudeStr = (String) addressInfo.get("x");

            // 위도와 경도가 비어 있거나 잘못된 형식인 경우 예외 처리
            if (latitudeStr == null || latitudeStr.isEmpty() || longitudeStr == null
                    || longitudeStr.isEmpty()) {
                log.error("위도 또는 경도 정보가 비어 있습니다. 주소: {}", address);
                throw new IllegalArgumentException("유효한 위도 또는 경도를 찾을 수 없습니다.");
            }

            try {
                // 위도와 경도를 double로 변환
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);
                return new LatLng(latitude, longitude);
            } catch (NumberFormatException e) {
                // 숫자 형식이 잘못된 경우 예외 처리
                log.error("위도 또는 경도 형식 오류. 주소: {}, 위도: {}, 경도: {}", address, latitudeStr,
                        longitudeStr, e);
                throw new IllegalArgumentException("위도 또는 경도의 형식이 잘못되었습니다.");
            }
        }

        // "documents"가 비어 있는 경우 또는 위도/경도를 찾을 수 없는 경우 예외 처리
        log.error("주소에서 유효한 위치 정보를 찾을 수 없습니다. 주소: {}", address);
        throw new IllegalArgumentException("주소에서 유효한 위치 정보를 찾을 수 없습니다.");
    }

    public record LatLng(Double latitude, Double longitude) {

    }
}
