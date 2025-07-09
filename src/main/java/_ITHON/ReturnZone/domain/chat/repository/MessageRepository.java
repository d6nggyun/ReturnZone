package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByChatRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    int countByChatRoomIdAndSenderIdNot(Long roomId, Long senderId);

    int countByChatRoomIdAndSenderIdNotAndCreatedAtAfter(
            Long roomId, Long senderId, LocalDateTime after);

    @Query(value = """
            SELECT m1.*
            FROM message m1
            JOIN (
                SELECT chat_room_id AS roomId, MAX(created_at) AS maxCreated
                FROM message
                WHERE chat_room_id IN (:roomIds)
                GROUP BY chat_room_id
            ) x
              ON m1.chat_room_id = x.roomId
             AND m1.created_at   = x.maxCreated
            """, nativeQuery = true)
    List<Message> findLastMessages(@Param("roomIds") List<Long> roomIds);

    @Modifying
    @Transactional
    @Query("delete from Message m where m.chatRoomId = :roomId")
    void deleteByChatRoomId(@Param("roomId") Long roomId);
}
