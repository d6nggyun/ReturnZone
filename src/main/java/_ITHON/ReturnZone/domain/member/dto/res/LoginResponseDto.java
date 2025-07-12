package _ITHON.ReturnZone.domain.member.dto.res;

import _ITHON.ReturnZone.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDto {

    @Schema(description = "사용자 Id", example = "1")
    private final Long memberId;

    @Schema(description = "로그인된 이메일", example = "gildong@gmail.com")
    private final String email;

    @Schema(description = "로그인된 이름", example = "홍길동")
    private final String username;

    @Schema(description = "로그인된 프로필 이미지 URL", example = "http://example.com/profile.jpg")
    private final String imageUrl;

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String accessToken;

    @Schema(description = "JWT Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String refreshToken;

    @Schema(description = "Access Token 만료 시간 (Epoch milliseconds)", example = "7195000")
    private final Long accessTokenExpires;

    @Schema(description = "Access Token 만료 일시 (Date)", example = "2025-05-14T15:30:00.000+09:00")
    private final Date accessTokenExpiresDate;

    @Builder
    private LoginResponseDto(Member member, String accessToken, String refreshToken, Long accessTokenExpires, Date accessTokenExpiresDate) {
        this.memberId = member.getId();
        this.email = member.getEmail();
        this.username = member.getUsername();
        this.imageUrl = member.getImageUrl();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpires = accessTokenExpires;
        this.accessTokenExpiresDate = accessTokenExpiresDate;
    }
}