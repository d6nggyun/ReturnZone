package _ITHON.ReturnZone.domain.chat.controller;

import _ITHON.ReturnZone.domain.chat.dto.req.AddChatRoomRequestDto;
import _ITHON.ReturnZone.domain.chat.dto.res.ChatRoomResponseDto;
import _ITHON.ReturnZone.domain.chat.dto.res.MessageResponseDto;
import _ITHON.ReturnZone.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Slice<ChatRoomResponseDto>> getChatRooms(@RequestHeader("X-USER-ID") Long myId,
                                                                   @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChatRooms(myId, page));
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
    public ResponseEntity<Slice<MessageResponseDto>> getChats(@RequestHeader("X-USER-ID") Long myId, @PathVariable Long roomId,
                                                              @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getChats(myId, roomId, page));
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
    public ResponseEntity<ChatRoomResponseDto> addChatRoom(@RequestHeader("X-USER-ID") Long myId,
                                                           @RequestBody AddChatRoomRequestDto request) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.addChatRoom(myId, request));
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방을 나가고 해당 채팅방을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "채팅방 나가기 성공")
            }
    )
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@RequestHeader("X-USER-ID") Long myId,
                                                              @PathVariable Long roomId) {
        chatService.deleteChatRoom(myId, roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "채팅방 읽음 처리", description = "채팅방 읽음 처리를 합니다. 해당 채팅방의 읽음 상태를 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "채팅방 읽음 처리 성공")
            }
    )
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markRead(@RequestHeader("X-USER-ID") Long myId,
                                         @PathVariable Long roomId) {
        chatService.markRead(myId, roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/rooms/{roomId}/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponseDto> uploadMessage(
            @PathVariable Long roomId,
            @RequestHeader("X-USER-ID") Long senderId,
            @RequestPart("image") MultipartFile imageFile) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(roomId, senderId, "", imageFile));
    }
}
