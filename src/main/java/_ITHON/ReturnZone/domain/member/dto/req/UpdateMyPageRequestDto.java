package _ITHON.ReturnZone.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "마이페이지 수정 요청 DTO")
public class UpdateMyPageRequestDto {

    @NotBlank(message = "닉네임이 비어있습니다.")
    @Schema(description = "닉네임", example = "유저1")
    private String nickname;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @NotBlank(message = "위치가 비어있습니다.")
    @Schema(description = "위치", example = "월계1동")
    private String location;

    @NotBlank(message = "상세 위치가 비어있습니다.")
    @Schema(description = "상세 위치", example = "주민 센터 앞")
    private String locationDetail;

    @NotBlank(message = "은행이 비어있습니다.")
    @Schema(description = "은행", example = "국민은행")
    private String bankName;

    @NotBlank(message = "계좌번호가 비어있습니다.")
    @Schema(description = "계좌번호", example = "1234-1234-1234")
    private String accountNumber;

    @NotBlank(message = "입금주 명이 비어있습니다.")
    @Schema(description = "입금주 명", example = "유저1")
    private String accountHolder;
}
