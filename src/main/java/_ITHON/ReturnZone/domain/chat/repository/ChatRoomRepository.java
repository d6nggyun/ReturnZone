package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Slice<ChatRoom> findByMemberAIdOrMemberBIdOrderByLastMessageAtDesc(
            Long memberAId, Long memberBId, Pageable pageable);

    // 마지막 메시지 시간 업데이트
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoom c SET c.lastMessageAt = :time WHERE c.id = :roomId")
    void updateLastMessageAt(@Param("roomId") Long roomId,
                             @Param("time") LocalDateTime time);
}
