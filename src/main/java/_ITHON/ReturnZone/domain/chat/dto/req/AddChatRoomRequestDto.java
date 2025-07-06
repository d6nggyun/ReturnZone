package _ITHON.ReturnZone.domain.chat.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "채팅방 생성 요청 DTO")
public class AddChatRoomRequestDto {

    @Schema(description = "채팅 상대 회원 ID", example = "17")
    private Long opponentId;
}
