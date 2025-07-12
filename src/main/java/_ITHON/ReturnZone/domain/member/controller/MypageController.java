package _ITHON.ReturnZone.domain.member.controller;

import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.member.dto.req.UpdateMyPageRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.ExchangeResponseDto;
import _ITHON.ReturnZone.domain.member.dto.res.MyPageResponseDto;
import _ITHON.ReturnZone.domain.member.service.MypageService;
import _ITHON.ReturnZone.global.response.SliceResponse;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @Operation(summary = "마이페이지 조회", description = "마이페이지를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponseDto.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getMyPage(userDetails.getMember().getId()));
    }

    @Operation(summary = "마이페이지 수정", description = "마이페이지를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "마이페이지 수정 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponseDto.class))
                    )
            }
    )
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyPageResponseDto> updateMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                          @Valid @RequestPart UpdateMyPageRequestDto updateMyPageRequestDto,
                                                          @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.updateMyPage(userDetails.getMember().getId(), updateMyPageRequestDto, image));
    }

    @Operation(summary = "환전하기", description = "환전을 요청합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "환전 요청 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponseDto.class))
                    )
            }
    )
    @PostMapping("/exchange")
    public ResponseEntity<MyPageResponseDto> exchange(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.exchange(userDetails.getMember().getId()));
    }

    @Operation(summary = "환전 요청 처리", description = "회원의 환전 요청을 처리합니다. (관리자 용)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "환전 요청 처리 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageResponseDto.class))
                    )
            }
    )
    @PatchMapping("/exchange/{exchangeId}")
    public ResponseEntity<MyPageResponseDto> processExchange(@PathVariable Long exchangeId,
                                                            @RequestParam boolean approve,
                                                            @RequestParam(required = false) String memo) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.processExchange(exchangeId, approve, memo));
    }

    @Operation(summary = "내가 등록한 분실물 조회", description = "내가 등록한 분실물들을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "내가 등록한 분실물 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleLostPostResponseDto.class)))
                    )
            }
    )
    @GetMapping("/lostPosts")
    public ResponseEntity<SliceResponse<SimpleLostPostResponseDto>> getMyLostPosts(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                   @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getMyLostPosts(userDetails.getMember().getId(), page));
    }

    @Operation(summary = "환전 내역 조회", description = "회원의 환전 내역을 조회합니다. (관리자 용)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "환전 내역 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ExchangeResponseDto.class)))
                    )
            }
    )
    @GetMapping("/exchange")
    public ResponseEntity<List<ExchangeResponseDto>> getMyExchanges(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(mypageService.getMyExchanges(userDetails.getMember().getId()));
    }
}
