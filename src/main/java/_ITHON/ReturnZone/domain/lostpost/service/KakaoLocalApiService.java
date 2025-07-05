package _ITHON.ReturnZone.domain.lostpost.service;

import _ITHON.ReturnZone.domain.lostpost.dto.res.KakaoAddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLocalApiService {

    @Value("${kakao.local.api-key}")
    private String kakaoLocalApiKey;

    @Value("${kakao.local.reverse-geocode-url}")
    private String reverseGeocodeUrl;

    private final RestTemplate restTemplate;

    /**
     * 위도, 경도를 이용하여 카카오 로컬 API를 통해 주소 정보를 가져옵니다.
     * @param longitude 경도 (x)
     * @param latitude 위도 (y)
     * @return 카카오 주소 응답 문서 (가장 첫 번째 결과)
     */
    public KakaoAddressResponse.Document getAddressFromCoordinates(Double longitude, Double latitude) {
        if (longitude == null || latitude == null) {
            log.warn("위도 또는 경도 값이 null이어서 주소 변환을 건너뜁니다.");
            return null;
        }

        // 요청 URL 생성 (경도 x, 위도 y)
        String url = UriComponentsBuilder.fromUriString(reverseGeocodeUrl)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .toUriString();

        // HTTP 헤더 설정 (Authorization: KakaoAK REST_API_KEY)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoLocalApiKey);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Kakao Local API 호출
            ResponseEntity<KakaoAddressResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    KakaoAddressResponse.class
            );

            // 응답 확인 및 필요한 주소 정보 추출
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KakaoAddressResponse kakaoResponse = response.getBody();
                log.info("카카오 API로부터 받은 원본 응답: {}", kakaoResponse);
                if (kakaoResponse.getDocuments() != null && !kakaoResponse.getDocuments().isEmpty()) {
                    return kakaoResponse.getDocuments().get(0); // 가장 정확한 첫 번째 결과 반환
                } else {
                    log.warn("카카오 API 응답에 documents가 비어있거나 없습니다. 응답: {}", kakaoResponse);
                }
            } else {
                log.warn("카카오 API 응답 상태 코드가 2xx가 아니거나 body가 null입니다. 상태: {}, 바디: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("카카오 로컬 API 호출 중 오류 발생: {}", e.getMessage(), e);
        }
        return null;
    }
}