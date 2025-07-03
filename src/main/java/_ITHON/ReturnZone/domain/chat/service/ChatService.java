package _ITHON.ReturnZone.domain.chat.service;

import _ITHON.ReturnZone.domain.chat.repository.ChatRoomRepository;
import _ITHON.ReturnZone.domain.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;


}
