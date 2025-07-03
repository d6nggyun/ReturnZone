package _ITHON.ReturnZone.global.config;

import _ITHON.ReturnZone.domain.member.dto.KakaoDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Component
@Getter
@Setter
@Slf4j
public class KakaoConfig {

    @Value("${kakao.auth.client-id}")
    private String clientId;

    @Value("${kakao.auth.redirect-uri}")
    private String redirectUri;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public KakaoDTO.OAuthToken requestKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoDTO.OAuthToken> response = restTemplate.postForEntity(
                    "https://kauth.kakao.com/oauth/token",
                    request,
                    KakaoDTO.OAuthToken.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("[카카오 토큰 요청 실패] 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("카카오 토큰 요청 실패: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("[카카오 토큰 요청 중 예외 발생] {}", e.getMessage());
            throw new RuntimeException("카카오 토큰 요청 중 오류 발생", e);
        }
    }

    public KakaoDTO.KakaoProfile requestKakaoUserProfile(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoDTO.KakaoProfile> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    request,
                    KakaoDTO.KakaoProfile.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("[카카오 사용자 정보 요청 실패] 상태 코드: {}", response.getStatusCode());
                throw new RuntimeException("카카오 사용자 정보 요청 실패: " + response.getStatusCode());
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("[카카오 사용자 정보 요청 중 예외 발생] {}", e.getMessage());
            throw new RuntimeException("카카오 사용자 정보 요청 중 오류 발생", e);
        }
    }
}