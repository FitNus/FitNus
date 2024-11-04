package com.sparta.modulecommon.center.service;

import com.sparta.modulecommon.center.exception.LocationNotFoundException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        if (responseBody != null && !((List<?>) responseBody.get("documents")).isEmpty()) {
            List<Map<String, Object>> documents = (List<Map<String, Object>>) responseBody.get(
                "documents");
            Map<String, Object> location = documents.get(0);
            Map<String, Object> addressInfo = (Map<String, Object>) location.get("address");

            double latitude = Double.parseDouble((String) addressInfo.get("y"));
            double longitude = Double.parseDouble((String) addressInfo.get("x"));
            return new LatLng(latitude, longitude);
        }
        throw new LocationNotFoundException();
    }

    public record LatLng(double latitude, double longitude) {}
}
