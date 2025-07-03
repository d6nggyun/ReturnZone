package _ITHON.ReturnZone.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberAId;

    @Column(nullable = false)
    private Long memberBId;

    private LocalDateTime lastMessageAt;

    // 읽음 처리용(선택)
    private LocalDateTime lastReadAtByA;
    private LocalDateTime lastReadAtByB;

    // 두 멤버가 같은 DM인지 조회
    public static String uniqueKey(Long a, Long b) {
        return (a < b) ? a + "_" + b : b + "_" + a; // 필요 시 Unique 인덱스로
    }
}
