package _ITHON.ReturnZone.domain.chat.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "채팅 요청 DTO")
public class SendMessageRequestDto {

    @NotNull(message = "채팅방 Id가 비어있습니다.")
    @Schema(description = "채팅방 Id", example = "1")
    private Long roomId;

    @NotBlank(message = "채팅 내용이 비어있습니다.")
    @Schema(description = "채팅 내용", example = "안녕하세요!")
    private String content;

    @NotBlank(message = "JWT 토큰이 누락되었습니다.")
    @Schema(description = "JWT 토큰", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;
}
