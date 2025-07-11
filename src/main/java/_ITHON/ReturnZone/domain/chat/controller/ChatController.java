package _ITHON.ReturnZone.domain.chat.controller;

import _ITHON.ReturnZone.domain.chat.dto.req.AddChatRoomRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.ChatRoomResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageContentResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.service.ChatService;
import _ITHON.ReturnZone.global.response.SliceResponse;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅방 목록 조회", description = "채팅방 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponseDto.class))
                    )
            }
    )
    @GetMapping("/rooms")
    public ResponseEntity<SliceResponse<ChatRoomResponseDto>> getChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChatRooms(userDetails.getMember().getId(), page));
    }

    @Operation(summary = "채팅 목록 조회", description = "채팅 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅 목록 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MessageResponseDto.class))
                    )
            }
    )
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageResponseDto> getChats(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId,
                                                        @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChats(userDetails.getMember().getId(), roomId, page));
    }

    @Operation(summary = "채팅방 생성", description = "채팅방을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅방 생성 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ChatRoomResponseDto.class))
                    )
            }
    )
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomResponseDto> addChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @RequestBody AddChatRoomRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.addChatRoom(userDetails.getMember().getId(), request));
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방을 나가고 해당 채팅방을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "채팅방 나가기 성공")
            }
    )
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                              @PathVariable Long roomId) {
        chatService.deleteChatRoom(userDetails.getMember().getId(), roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "채팅방 읽음 처리", description = "채팅방 읽음 처리를 합니다. 해당 채팅방의 읽음 상태를 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅방 읽음 처리 성공")
            }
    )
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable Long roomId) {
        chatService.markRead(userDetails.getMember().getId(), roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/rooms/{roomId}/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageContentResponseDto> uploadMessage(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("image") MultipartFile imageFile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(roomId, userDetails.getMember().getId(), "", imageFile));
    }
}
