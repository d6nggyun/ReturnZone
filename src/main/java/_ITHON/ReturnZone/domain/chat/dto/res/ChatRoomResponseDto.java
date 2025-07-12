package _ITHON.ReturnZone.domain.chat.dto.res;

import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import _ITHON.ReturnZone.domain.lostpost.entity.Status;
import _ITHON.ReturnZone.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(description = "채팅방 응답 DTO")
public class ChatRoomResponseDto {

    @Schema(description = "채팅방 ID", example = "1")
    private final Long roomId;

    @Schema(description = "상대방 회원 ID", example = "2")
    private final Long otherMemberId;

    @Schema(description = "상대방 닉네임", example = "honggildong")
    private final String otherMemberNickname;

    @Schema(description = "상대방 프로필 이미지 URL", example = "/images/profile/2.png")
    private final String otherMemberProfileImage;

    @Schema(description = "마지막 메시지 내용", example = "안녕하세요!")
    private final String lastMessage;

    @Schema(description = "마지막 메시지 전송 시간", example = "2025-07-05T18:00:00")
    private final LocalDateTime lastMessageAt;

    @Schema(description = "읽지 않은 메시지 수", example = "3")
    private final int unreadCount;

    @Schema(description = "분실물 상태", example = "주인 찾는 중")
    private final String lostPostStatus;

    @Builder
    public ChatRoomResponseDto(ChatRoom chatRoom, Member otherMember, String lastMessage, int unreadCount, Status lostPostStatus) {
        this.roomId = chatRoom.getId();
        this.otherMemberId = otherMember.getId();
        this.otherMemberNickname = otherMember.getNickname();
        this.otherMemberProfileImage = otherMember.getImageUrl();
        this.lastMessage = lastMessage;
        this.lastMessageAt = chatRoom.getLastMessageAt();
        this.unreadCount = unreadCount;
        this.lostPostStatus = lostPostStatus != null ? lostPostStatus.getDescription() : null;
    }
}
