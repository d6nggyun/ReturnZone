package _ITHON.ReturnZone.domain.chat.controller;

import _ITHON.ReturnZone.domain.chat.dto.req.SendMessageRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageContentResponseDto;
import _ITHON.ReturnZone.domain.chat.service.ChatService;
import _ITHON.ReturnZone.global.security.jwt.JwtTokenProvider;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // 클라이언트 전송 주소: /app/chat.send
    @MessageMapping("/chat.send")
    public void handleMessage(SendMessageRequestDto request, @Header("Authorization") String accessToken) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("텍스트 내용이 비어 있습니다.");
        }

        // JWT로부터 사용자 인증
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("JWT 토큰이 누락되었습니다.");
        }

        String token = accessToken.replace("Bearer ", "");
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        Long senderId = ((UserDetailsImpl) auth.getPrincipal()).getMember().getId();

        // 채팅 메시지 처리
        MessageContentResponseDto response = chatService.sendMessage(request.getRoomId(), senderId, request.getContent(), null);

        // WebSocket을 통해 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), response);
    }
}
