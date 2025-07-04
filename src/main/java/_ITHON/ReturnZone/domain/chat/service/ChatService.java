package _ITHON.ReturnZone.domain.chat.service;

import _ITHON.ReturnZone.domain.chat.dto.req.SendMessageRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.ChatRoomResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import _ITHON.ReturnZone.domain.chat.entity.Message;
import _ITHON.ReturnZone.domain.chat.repository.ChatRoomRepository;
import _ITHON.ReturnZone.domain.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public MessageResponseDto sendMessage(SendMessageRequestDto request) {
        // 메시지 저장
        Message message = messageRepository.save(
                Message.builder().request(request).build()
        );

        // 방 메타 업데이트
        chatRoomRepository.updateLastMessageAt(request.getRoomId(), message.getCreatedAt());

        // 응답 DTO
        return MessageResponseDto.builder().message(message).build();
    }

    @Transactional
    public void markRead(Long roomId, Long readerId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow();
        if (readerId.equals(chatRoom.getMemberAId())) chatRoomRepository.setLastReadAtByA(LocalDateTime.now());
        else chatRoom.setLastReadAtByB(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Slice<ChatRoomResponseDto> getChatRooms(Long memberId, Pageable pageable) {

        Slice<ChatRoom> chatRoomSlice =
                chatRoomRepository.findByMemberAIdOrMemberBIdOrderByLastMessageAtDesc(memberId, memberId, pageable);

        return chatRoomSlice.stream().map(chatRoom -> ChatRoomResponseDto.builder);
    }

    @Transactional(readOnly = true)
    public Slice<MessageResponseDto> getChats(Long roomId, Pageable pageable) {

        Slice<Message> messageSlice =
                messageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);

        return messageSlice.stream()
                .map(message -> MessageResponseDto.builder().message(message).build())
                .toList();
    }
}
