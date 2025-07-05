package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByChatRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    Message findFirstByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    int countByChatRoomIdAndSenderIdNot(Long roomId, Long senderId);

    int countByChatRoomIdAndSenderIdNotAndCreatedAtAfter(
            Long roomId, Long senderId, LocalDateTime after);
}
