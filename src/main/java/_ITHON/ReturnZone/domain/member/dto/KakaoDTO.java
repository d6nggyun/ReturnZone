package _ITHON.ReturnZone.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class KakaoDTO {

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OAuthToken {
        @JsonProperty("access_token")
        private String access_token;
        @JsonProperty("token_type")
        private String token_type;
        @JsonProperty("refresh_token")
        private String refresh_token;
        @JsonProperty("expires_in")
        private Integer expires_in;
        @JsonProperty("scope")
        private String scope;
        @JsonProperty("refresh_token_expires_in")
        private Integer refresh_token_expires_in;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoProfile {
        private Long id;
        @JsonProperty("connected_at")
        private String connectedAt;
        @JsonProperty("kakao_account")
        private KakaoAccount kakao_account;

        @Getter
        @Setter
        @ToString
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoAccount {
            @JsonProperty("profile_needs_agreement")
            private Boolean profileNeedsAgreement;
            private Profile profile;
            @JsonProperty("email_needs_agreement")
            private Boolean emailNeedsAgreement;
            private String email;

            @Getter
            @Setter
            @ToString
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Profile {
                private String nickname;
                @JsonProperty("thumbnail_image_url")
                private String thumbnailImageUrl;
                @JsonProperty("profile_image_url")
                private String profileImageUrl;
                @JsonProperty("is_default_image")
                private Boolean isDefaultImage;
            }
        }
    }
}