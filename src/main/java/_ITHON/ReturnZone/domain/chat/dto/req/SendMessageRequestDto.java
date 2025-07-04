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

    @NotNull(message = "전송자 Id가 비어있습니다.")
    @Schema(description = "전송자 Id", example = "1")
    private Long senderId;

    @NotBlank(message = "채팅 내용이 비어있습니다.")
    @Schema(description = "채팅 내용", example = "안녕하세요!")
    private String content;

    @NotBlank(message = "이미지가 비어있습니다.")
    @Schema(description = "채팅 이미지")
    private String imageUrl;
}
