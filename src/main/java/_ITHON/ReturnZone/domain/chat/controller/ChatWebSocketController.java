package _ITHON.ReturnZone.domain.chat.controller;

import _ITHON.ReturnZone.domain.chat.dto.req.SendMessageRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트 전송 주소: /app/chat.send
    @MessageMapping("/chat.send")
    public void handleMessage(SendMessageRequestDto request) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("텍스트 내용이 비어 있습니다.");
        }

        // 채팅 메시지 처리
        MessageResponseDto response = chatService.sendMessage(request.getRoomId(), request.getSenderId(), request.getContent(), null);

        // WebSocket을 통해 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + response.getRoomId(), response);
    }
}
