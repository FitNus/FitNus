package com.sparta.service.center.service;

import com.sparta.service.center.repository.CenterCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestTemplate restTemplate;
    private final CenterCacheRepository cacheRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    /**
     * 주소를 위도와 경도로 변환하고, 결과를 캐싱합니다.
     *
     * @param address 변환할 주소
     * @return 위도와 경도 정보를 담은 LatLng 객체
     */
    public LatLng getLatLngFromAddress(String address) {
        // 1. 캐시에서 위도/경도 정보 조회
        LatLng cachedLatLng = cacheRepository.getCachedLocation(address);
        if (cachedLatLng != null) {
            log.info("캐시에서 위치 정보 조회: {}", cachedLatLng);
            return cachedLatLng;
        }

        // 2. 캐시에 없을 경우 Kakao API 호출
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, (Class<Map<String, Object>>) (Class<?>) Map.class);
            Map<String, Object> responseBody = response.getBody();

            // 3. API 응답에서 위도/경도 정보 추출
            if (responseBody != null && !((List<?>) responseBody.get("documents")).isEmpty()) {
                List<Map<String, Object>> documents = (List<Map<String, Object>>) responseBody.get("documents");
                Map<String, Object> location = documents.get(0);
                Map<String, Object> addressInfo = (Map<String, Object>) location.get("address");

                String latitudeStr = (String) addressInfo.get("y");
                String longitudeStr = (String) addressInfo.get("x");

                // 4. 위도와 경도가 유효한지 검증
                if (latitudeStr == null || longitudeStr == null) {
                    log.error("위도 또는 경도 정보가 비어 있습니다. 주소: {}", address);
                    throw new IllegalArgumentException("유효한 위도 또는 경도를 찾을 수 없습니다.");
                }

                // 5. 위도와 경도를 double로 변환하고 LatLng 객체 생성
                double latitude = Double.parseDouble(latitudeStr);
                double longitude = Double.parseDouble(longitudeStr);
                LatLng latLng = new LatLng(latitude, longitude);

                // 6. 변환 결과를 캐싱 (TTL: 1시간)
                cacheRepository.cacheLocation(address, latLng, 3600);
                log.info("위치 정보 캐싱 완료: {}", latLng);

                return latLng;
            } else {
                log.error("Kakao API 응답에서 유효한 위치 정보를 찾을 수 없습니다. 주소: {}", address);
                throw new IllegalArgumentException("주소에서 유효한 위치 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("Kakao API 호출 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("Kakao API 호출 실패", e);
        }
    }

    /**
     * 위도와 경도 정보를 담는 Record 클래스
     */
    public record LatLng(Double latitude, Double longitude) {}
}
