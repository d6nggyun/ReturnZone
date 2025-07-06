package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN FETCH Member m
             ON m.id = CASE
                         WHEN cr.smallerMemberId = :memberId
                         THEN cr.greaterMemberId
                         ELSE cr.smallerMemberId
                       END
        WHERE cr.smallerMemberId = :memberId
           OR cr.greaterMemberId = :memberId
        ORDER BY cr.lastMessageAt DESC
    """)
    Slice<ChatRoom> findRoomsWithOpponent(@Param("memberId") Long memberId, Pageable pageable);

    // 마지막 메시지 시간 업데이트
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatRoom c SET c.lastMessageAt = :time WHERE c.id = :roomId")
    void updateLastMessageAt(@Param("roomId") Long roomId,
                             @Param("time") LocalDateTime time);

    Optional<ChatRoom> findBySmallerMemberIdAndGreaterMemberId(Long smallerMemberId, Long greaterMemberId);
}
