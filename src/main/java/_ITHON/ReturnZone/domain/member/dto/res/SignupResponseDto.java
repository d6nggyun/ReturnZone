package _ITHON.ReturnZone.domain.member.dto.res;

import _ITHON.ReturnZone.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 응답 DTO")
public class SignupResponseDto {

    @Schema(description = "회원가입된 이메일", example = "dongkyun@gmail.com")
    private final String email;

    @Schema(description = "회원가입된 닉네임", example = "두옹균")
    private final String nickname;

    @Builder
    private SignupResponseDto(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}
