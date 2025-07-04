package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Slice<ChatRoom> findByMemberAIdOrMemberBIdOrderByLastMessageAtDesc(
            Long memberAId, Long memberBId, Pageable pageable);
}
