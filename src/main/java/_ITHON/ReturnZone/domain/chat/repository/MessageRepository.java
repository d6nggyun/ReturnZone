package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByChatRoomId(Long roomId, Pageable pageable);

    Slice<Message> findByChatRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
}
