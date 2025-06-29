package _ITHON.ReturnZone.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDto {

    @NotBlank(message = "이름이 비어있습니다.")
    @Schema(description = "사용자의 이름", example = "홍길동")
    private String username;

    @NotBlank(message = "이메일이 비어있습니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Schema(description = "사용자의 이메일 주소", example = "gildong@gmail.com")
    private String email;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "사용자의 비밀번호 (최소 8자 이상)", example = "password123")
    private String password;
}
