package _ITHON.ReturnZone.domain.chat.repository;

import _ITHON.ReturnZone.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
