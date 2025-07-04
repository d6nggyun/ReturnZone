package _ITHON.ReturnZone.domain.chat.dto.res;

import _ITHON.ReturnZone.domain.chat.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "채팅 응답 DTO")
public class MessageResponseDto {

    @Schema(description = "채팅 메시지 Id", example = "1")
    private final Long messageId;

    @Schema(description = "채팅방 Id", example = "1")
    private final Long roomId;

    @Schema(description = "전송자 Id", example = "1")
    private final Long senderId;

    @Schema(description = "채팅 내용", example = "안녕하세요!")
    private final String content;

    @Schema(description = "채팅 이미지 URL")
    private final String imageUrl;

    @Schema(description = "채팅 전송 시간", example = "2023-10-01T12:00:00")
    private final LocalDateTime createdAt;

    @Builder
    private MessageResponseDto(Message message) {
        this.messageId = message.getId();
        this.roomId = message.getChatRoomId();
        this.senderId = message.getSenderId();
        this.content = message.getContent();
        this.imageUrl = message.getImageUrl();
        this.createdAt = message.getCreatedAt();
    }
}
