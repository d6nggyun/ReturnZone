package _ITHON.ReturnZone.domain.chat.service;

import _ITHON.ReturnZone.domain.chat.dto.req.SendMessageRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.ChatRoomResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import _ITHON.ReturnZone.domain.chat.entity.Message;
import _ITHON.ReturnZone.domain.chat.repository.ChatRoomRepository;
import _ITHON.ReturnZone.domain.chat.repository.MessageRepository;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MessageResponseDto sendMessage(SendMessageRequestDto request) {
        // 메시지 저장
        Message message = messageRepository.save(Message.builder().request(request).build());

        // 방 메타 업데이트
        chatRoomRepository.updateLastMessageAt(request.getRoomId(), message.getCreatedAt());

        // 응답 DTO
        return MessageResponseDto.builder().message(message).build();
    }

    @Transactional(readOnly = true)
    public Slice<ChatRoomResponseDto> getChatRooms(Long memberId, Pageable pageable) {

        Slice<ChatRoom> chatRoomSlice =
                chatRoomRepository.findByMemberAIdOrMemberBIdOrderByLastMessageAtDesc(memberId, memberId, pageable);

        return chatRoomSlice.map(chatRoom -> {
            // 상대방 ID 계산
            Long otherId = chatRoom.getMemberAId().equals(memberId) ? chatRoom.getMemberBId() : chatRoom.getMemberAId();
            Member otherMember = memberRepository.findById(otherId)
                    .orElseThrow(() -> {
                        log.warn("[회원 조회 실패] 존재하지 않는 회원 Id: {}", otherId);
                        return new IllegalArgumentException("채팅방 상대 회원이 존재하지 않습니다.");
                    });

            // (옵션) 마지막 메시지, unread 계산
            Message lastMsg = messageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc((chatRoom.getId()));

            String lastMsgContent = lastMsg == null ? null : lastMsg.getContent();

            LocalDateTime lastRead = chatRoom.getMemberAId().equals(memberId)
                    ? chatRoom.getLastReadAtByA()
                    : chatRoom.getLastReadAtByB();

            int unreadCnt = (lastRead == null)
                    ? messageRepository.countByChatRoomIdAndSenderIdNot(chatRoom.getId(), memberId)
                    : messageRepository.countByChatRoomIdAndSenderIdNotAndCreatedAtAfter(
                    chatRoom.getId(), memberId, lastRead);


            return ChatRoomResponseDto.builder()
                    .chatRoom(chatRoom).otherMember(otherMember)
                    .lastMessage(lastMsgContent).unreadCount(unreadCnt).build();
        });
    }

    @Transactional(readOnly = true)
    public Slice<MessageResponseDto> getChats(Long roomId, Pageable pageable) {

        Slice<Message> messageSlice =
                messageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);

        return messageSlice.map(message -> MessageResponseDto.builder().message(message).build());
    }
}
