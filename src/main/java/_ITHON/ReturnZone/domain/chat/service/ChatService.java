package _ITHON.ReturnZone.domain.chat.service;

import _ITHON.ReturnZone.domain.chat.dto.req.AddChatRoomRequestDto;
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
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MessageResponseDto sendMessage(Long roomId, Long senderId, String content, MultipartFile image) {

        log.info("[메시지 전송 요청] 채팅방 ID: {}, 보낸 사람 ID: {}, 내용: {}", roomId, senderId, content);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            // 파일 업로드 로직 (예: S3에 업로드)
            // imageUrl = fileUploadService.uploadFile(file);
            log.info("[파일 업로드] 파일 이름: {}", image.getOriginalFilename());
            // 실제 업로드 로직은 구현 필요
        }

        // 메시지 저장
        Message message = messageRepository.save(Message.builder()
                .roomId(roomId).senderId(senderId).content(content).imageUrl(imageUrl).build());

        // 방 메타 업데이트
        chatRoomRepository.updateLastMessageAt(roomId, message.getCreatedAt());

        log.info("[메시지 전송 성공] 채팅방 ID: {}, 메시지 ID: {}", roomId, message.getId());

        // 응답 DTO
        return MessageResponseDto.builder().message(message).build();
    }

    @Transactional(readOnly = true)
    public Slice<ChatRoomResponseDto> getChatRooms(Long memberId, Pageable pageable) {

        log.info("[채팅방 목록 조회 요청] 회원 ID: {}", memberId);

        Slice<ChatRoom> chatRoomSlice = chatRoomRepository.findRoomsWithOpponent(memberId, pageable);

        if (chatRoomSlice.isEmpty()) return new SliceImpl<>(List.of(), pageable, false);

        List<Long> roomIds = chatRoomSlice.stream().map(ChatRoom::getId).distinct().toList();
        List<Long> opponentIds = chatRoomSlice.stream()
                .map(r -> r.opponentOf(memberId)).distinct().toList();

        Map<Long, Member> opponentMap = memberRepository.findAllById(opponentIds)
                .stream().collect(Collectors.toMap(Member::getId, m -> m));

        Map<Long, Message> lastMsgMap = messageRepository.findLastMessages(roomIds)
                .stream().collect(Collectors.toMap(Message::getChatRoomId, m -> m));

        return chatRoomSlice.map(chatRoom -> {
            // 상대방 ID 계산
            Long otherId = chatRoom.opponentOf(memberId);
            Member otherMember = opponentMap.get(otherId);
            if (otherMember == null) {
                log.warn("존재하지 않는 상대방 id={}", otherId);
                throw new IllegalArgumentException("채팅방 상대 회원이 존재하지 않습니다.");
            }

            // 마지막 메시지, unread 계산
            Message lastMsg = lastMsgMap.get(chatRoom.getId());
            String lastMsgContent = lastMsg == null ? null : lastMsg.getContent();

            LocalDateTime lastRead = chatRoom.getLastReadAt(memberId);
            int unreadCnt = (lastRead == null)
                    ? messageRepository.countByChatRoomIdAndSenderIdNot(chatRoom.getId(), memberId)
                    : messageRepository.countByChatRoomIdAndSenderIdNotAndCreatedAtAfter(
                    chatRoom.getId(), memberId, lastRead);

            log.info("[채팅방 목록 조회 성공] 채팅방 ID: {}, 상대방 ID: {}, 마지막 메시지: {}, 읽지 않은 메시지 수: {}",
                    chatRoom.getId(), otherId, lastMsgContent, unreadCnt);

            return ChatRoomResponseDto.builder()
                    .chatRoom(chatRoom).otherMember(otherMember)
                    .lastMessage(lastMsgContent).unreadCount(unreadCnt).build();
        });
    }

    @Transactional(readOnly = true)
    public Slice<MessageResponseDto> getChats(Long roomId, Pageable pageable) {

        log.info("[채팅 목록 조회 요청] 채팅방 ID: {}", roomId);

        Slice<Message> messageSlice =
                messageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);

        log.info("[채팅 목록 조회 성공]");

        return messageSlice.map(message -> MessageResponseDto.builder().message(message).build());
    }

    @Transactional
    public ChatRoomResponseDto addChatRoom(Long myId, AddChatRoomRequestDto request) {

        log.info("[채팅방 생성 요청] 내 ID: {}, 상대방 ID: {}", myId, request.getOpponentId());

        if (myId.equals(request.getOpponentId())) {
            throw new IllegalArgumentException("자기 자신과는 채팅방을 만들 수 없습니다.");
        }

        Member otherMember = memberRepository.findById(request.getOpponentId())
                .orElseThrow(() -> {
                    log.warn("[회원 조회 실패] 존재하지 않는 회원 Id: {}", request.getOpponentId());
                    return new IllegalArgumentException("채팅방 상대 회원이 존재하지 않습니다.");
                });

        long smaller = Math.min(myId, request.getOpponentId());
        long greater = Math.max(myId, request.getOpponentId());

        ChatRoom chatRoom = chatRoomRepository
                .findBySmallerMemberIdAndGreaterMemberId(smaller, greater)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.of(smaller, greater)));

        log.info("[채팅방 생성 성공]");

        return ChatRoomResponseDto.builder().chatRoom(chatRoom).otherMember(otherMember).lastMessage(null).unreadCount(0).build();
    }

    @Transactional
    public void deleteChatRoom(Long myId, Long roomId) {

        log.info("[채팅방 삭제 요청] 내 ID: {}, 채팅방 ID: {}", myId, roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회 실패] 존재하지 않는 채팅방 ID: {}", roomId);
                    return new IllegalArgumentException("존재하지 않는 채팅방입니다.");
                });

        if (!chatRoom.getSmallerMemberId().equals(myId) && !chatRoom.getGreaterMemberId().equals(myId)) {
            log.warn("[채팅방 삭제 실패] 내 ID: {}, 채팅방 ID: {}에 대한 권한 없음", myId, roomId);
            throw new IllegalArgumentException("해당 채팅방에 대한 권한이 없습니다.");
        }

        chatRoomRepository.delete(chatRoom);

        log.info("[채팅방 삭제 성공]");
    }

    @Transactional
    public void markRead(Long myId, Long roomId) {

        log.info("[채팅방 읽음 처리 요청] 내 ID: {}, 채팅방 ID: {}", myId, roomId);

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회 실패] 존재하지 않는 채팅방 ID: {}", roomId);
                    return new IllegalArgumentException("존재하지 않는 채팅방입니다.");
                });

        chatRoom.markRead(myId);

        log.info("[채팅방 읽음 처리 성공]");
    }


}
