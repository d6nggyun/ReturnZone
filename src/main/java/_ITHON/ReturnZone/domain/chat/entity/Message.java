package _ITHON.ReturnZone.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 1000)
    private String content;

    private String imageUrl;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Message(Long roomId, Long senderId, String content, String imageUrl) {
        this.chatRoomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
