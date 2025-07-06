package _ITHON.ReturnZone.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "chat_room",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_chat_pair",
                columnNames = {"smaller_member_id", "greater_member_id"}
        ),
        indexes = @Index(name = "idx_lastMessageAt", columnList = "lastMessageAt DESC")
)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "smaller_member_id", nullable = false)
    private Long smallerMemberId;

    @Column(name = "greater_member_id", nullable = false)
    private Long greaterMemberId;

    @Column(name = "last_message_at", nullable = false)
    private LocalDateTime lastMessageAt;

    @Column(name = "last_read_at_smaller")
    private LocalDateTime lastReadAtSmaller;

    @Column(name = "last_read_at_greater")
    private LocalDateTime lastReadAtGreater;

    public static ChatRoom of(Long aId, Long bId) {
        ChatRoom room = new ChatRoom();
        if (aId < bId) {
            room.smallerMemberId = aId;
            room.greaterMemberId = bId;
        } else {
            room.smallerMemberId = bId;
            room.greaterMemberId = aId;
        }
        room.lastMessageAt = LocalDateTime.now();
        return room;
    }

    public Long opponentOf(Long memberId) {
        return memberId.equals(smallerMemberId) ? greaterMemberId : smallerMemberId;
    }

    public LocalDateTime getLastReadAt(Long memberId) {
        return memberId.equals(smallerMemberId) ? lastReadAtSmaller : lastReadAtGreater;
    }

    public void markRead(Long memberId) {
        if (memberId.equals(smallerMemberId))
            this.lastReadAtSmaller = LocalDateTime.now();
        else
            this.lastReadAtGreater = LocalDateTime.now();
    }
}
