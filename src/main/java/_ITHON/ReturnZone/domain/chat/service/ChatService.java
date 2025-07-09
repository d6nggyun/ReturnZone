package _ITHON.ReturnZone.domain.chat.service;

import _ITHON.ReturnZone.domain.chat.dto.req.AddChatRoomRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.ChatRoomResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageContentResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.entity.ChatRoom;
import _ITHON.ReturnZone.domain.chat.entity.Message;
import _ITHON.ReturnZone.domain.chat.repository.ChatRoomRepository;
import _ITHON.ReturnZone.domain.chat.repository.MessageRepository;
import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.entity.Status;
import _ITHON.ReturnZone.domain.lostpost.repository.LostPostRepository;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import _ITHON.ReturnZone.global.aws.s3.AwsS3Uploader;
import _ITHON.ReturnZone.global.response.PageInfo;
import _ITHON.ReturnZone.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final SimpMessagingTemplate messagingTemplate;
    private final LostPostRepository lostPostRepository;

    @Transactional
    public MessageContentResponseDto sendMessage(Long roomId, Long senderId, String content, MultipartFile image) {

        log.info("[메시지 전송 요청] 채팅방 ID: {}, 보낸 사람 ID: {}, 내용: {}", roomId, senderId, content);

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = awsS3Uploader.upload(image, "chats");
            } catch (IOException e) {
                log.error("이미지 파일 S3 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("이미지 파일 업로드에 실패했습니다.", e);
            }
        }

        // 메시지 저장
        Message message = messageRepository.save(Message.builder()
                .roomId(roomId).senderId(senderId).content(content).imageUrl(imageUrl).build());

        // 방 메타 업데이트
        chatRoomRepository.updateLastMessageAt(roomId, message.getCreatedAt());

        // 구독중인 클라이언트에게 push
        MessageContentResponseDto messageContentResponseDto = MessageContentResponseDto.builder().message(message).build();
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, messageContentResponseDto);

        log.info("[메시지 전송 성공] 채팅방 ID: {}, 메시지 ID: {}", roomId, message.getId());

        // 응답 DTO
        return messageContentResponseDto;
    }

    @Transactional(readOnly = true)
    public SliceResponse<ChatRoomResponseDto> getChatRooms(Long memberId, int page) {

        log.info("[채팅방 목록 조회 요청] 회원 ID: {}", memberId);

        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "lastMessageAt"));

        // 채팅방 목록 조회
        Slice<ChatRoom> chatRoomSlice = chatRoomRepository.findRoomsWithOpponent(memberId, pageable);

        // 없으면 빈 슬라이스 반환
        if (chatRoomSlice.isEmpty()) return new SliceResponse<>(List.of(), new PageInfo(page, 20, false));

        // 채팅방 ID, 상대방 ID, 분실물 ID 목록 추출
        List<Long> roomIds = chatRoomSlice.stream().map(ChatRoom::getId).distinct().toList();
        List<Long> opponentIds = chatRoomSlice.stream().map(r -> r.opponentOf(memberId)).distinct().toList();
        List<Long> lostPostIds = chatRoomSlice.stream().map(ChatRoom::getLostPostId).distinct().toList();

        // 상대방 정보와 마지막 메시지, 분실물 상태 정보 조회
        Map<Long, Member> opponentMap = memberRepository.findAllById(opponentIds)
                .stream().collect(Collectors.toMap(Member::getId, m -> m));
        Map<Long, Message> lastMsgMap = messageRepository.findLastMessages(roomIds)
                .stream().collect(Collectors.toMap(Message::getChatRoomId, m -> m));
        Map<Long, Status> lostPostStatusMap = lostPostRepository.findAllById(lostPostIds)
                .stream().collect(Collectors.toMap(LostPost::getId, LostPost::getStatus));

        Slice<ChatRoomResponseDto> chatRoomResponseDtos = chatRoomSlice.map(chatRoom -> {
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

            Status lostPostStatus = Optional.ofNullable(lostPostStatusMap.get(chatRoom.getLostPostId()))
                            .orElse(Status.FINDING_OWNER);

            return ChatRoomResponseDto.builder()
                    .chatRoom(chatRoom).otherMember(otherMember)
                    .lastMessage(lastMsgContent).unreadCount(unreadCnt).lostPostStatus(lostPostStatus).build();
        });

        log.info("[채팅방 목록 조회 성공] 회원 ID: {}", memberId);

        return SliceResponse.from(chatRoomResponseDtos);
    }

    @Transactional(readOnly = true)
    public MessageResponseDto getChats(Long myId, Long roomId, int page) {

        log.info("[채팅 목록 조회 요청] 채팅방 ID: {}", roomId);

        Pageable pageable = PageRequest.of(page, 30, Sort.by(Sort.Direction.DESC, "createdAt"));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.warn("[채팅방 조회 실패] 존재하지 않는 채팅방 ID: {}", roomId);
                    return new IllegalArgumentException("존재하지 않는 채팅방");
                });
        if (!room.isParticipant(myId)) {
            throw new IllegalArgumentException("채팅방에 대한 권한이 없습니다.");
        }

        LostPost lostPost = lostPostRepository.findById(room.getLostPostId())
                .orElseThrow(() -> {
                    log.warn("[분실물 조회 실패] 존재하지 않는 분실물 ID: {}", room.getLostPostId());
                    return new IllegalArgumentException("존재하지 않는 분실물입니다.");
                });

        SimpleLostPostResponseDto lostPostResponseDto = SimpleLostPostResponseDto.builder().lostPost(lostPost).build();

        Slice<Message> messageSlice =
                messageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);

        Slice<MessageContentResponseDto> messageContentResponseDtos =
                messageSlice.map(message -> MessageContentResponseDto.builder().message(message).build());

        log.info("[채팅 목록 조회 성공]");

        return MessageResponseDto.builder().roomId(roomId).lostPost(lostPostResponseDto).messages(messageContentResponseDtos)
                .page(new PageInfo(messageSlice.getNumber(), messageSlice.getSize(), messageSlice.hasNext())).build();
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

        LostPost lostPost = lostPostRepository.findById(request.getLostPostId())
                .orElseThrow(() -> {
                    log.warn("[분실물 조회 실패] 존재하지 않는 분실물 ID: {}", request.getLostPostId());
                    return new IllegalArgumentException("존재하지 않는 분실물입니다.");
                });

        ChatRoom chatRoom = chatRoomRepository
                .findBySmallerMemberIdAndGreaterMemberId(smaller, greater)
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.of(smaller, greater, lostPost.getId())));

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

        messageRepository.deleteByChatRoomId(roomId);

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