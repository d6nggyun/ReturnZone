package _ITHON.ReturnZone.domain.chat.dto.res;

import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.global.response.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Schema(description = "채팅 정보 응답 DTO")
public class MessageResponseDto {

    @Schema(description = "채팅방 Id", example = "1")
    private final Long roomId;

    @Schema(description = "분실물 정보")
    private final SimpleLostPostResponseDto lostPost;

    @Schema(description = "채팅 내용")
    private final Slice<MessageContentResponseDto> messages;

    private final PageInfo page;

    @Builder
    private MessageResponseDto(Long roomId, SimpleLostPostResponseDto lostPost, Slice<MessageContentResponseDto> messages, PageInfo page) {
        this.roomId = roomId;
        this.lostPost = lostPost;
        this.messages = messages;
        this.page = page;
    }
}
