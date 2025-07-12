package _ITHON.ReturnZone.domain.lostpost.controller;

import _ITHON.ReturnZone.domain.lostpost.dto.req.LostPostRequestDto;
import _ITHON.ReturnZone.domain.lostpost.dto.res.KakaoAddressResponse;
import _ITHON.ReturnZone.domain.lostpost.dto.res.LostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.entity.RegistrationType;
import _ITHON.ReturnZone.domain.lostpost.entity.SortType;
import _ITHON.ReturnZone.domain.lostpost.service.KakaoLocalApiService;
import _ITHON.ReturnZone.domain.lostpost.service.LostPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lostPosts")
@Tag(name = "LostPost", description = "분실물 관련 API")
public class LostPostController {

    private final LostPostService lostPostService;
    private final KakaoLocalApiService kakaoLocalApiService;

    @Operation(summary = "분실물 목록 조회", description = "분실물 목록을 조회합니다. (기본값은 최신순 정렬)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "분실물 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleLostPostResponseDto.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Slice<SimpleLostPostResponseDto>> getLostPostList(
            @Parameter(description = "주인 찾아요 / 분실했어요", example = "LOST / FOUND")
            @RequestParam(required = false) RegistrationType registrationType,

            @Parameter(description = "정렬 방식<br>• **LATEST**: 최신순<br>• **DISTANCE**: 거리순(위·경도 필수)", schema = @Schema(defaultValue = "LATEST", allowableValues = {"LATEST","DISTANCE"}))
            @RequestParam(defaultValue = "LATEST") SortType sort,

            @Parameter(description = "현재 위치 **위도**(거리순 정렬 시 필수)", example = "37.5665")
            @RequestParam(required = false) Double latitude,

            @Parameter(description = "현재 위치 **경도**(거리순 정렬 시 필수)", example = "126.9780")
            @RequestParam(required = false) Double longitude,

            @Parameter(description = "`true` : 즉시 정산 가능 글만", example = "true")
            @RequestParam(required = false) Boolean instant,

            @Parameter(description = "카테고리 필터 (예: 전자기기, 지갑/가방)", example = "전자기기")
            @RequestParam(required = false) String category,

            @ParameterObject
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(lostPostService.getLostPostList(registrationType, sort, latitude, longitude, instant, category, pageable));
    }

    @Operation(summary = "분실물 정보 상세 조회", description = "분실물의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "분실물 정보 상세 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LostPostResponseDto.class))
                    )
            }
    )
    @GetMapping("/{lostPostId}")
    public ResponseEntity<LostPostResponseDto> getLostPost(@PathVariable Long lostPostId) {
        return ResponseEntity.status(HttpStatus.OK).body(lostPostService.getLostPost(lostPostId));
    }

    @Operation(summary = "카카오 맵 확인용 좌표로 동(Dong) 정보 조회 (테스트용)", description = "위도와 경도를 입력받아 카카오맵 API를 통해 동(Dong) 정보를 포함한 주소를 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주소 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = KakaoAddressResponse.Document.class))),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 좌표", content = @Content)
            }
    )
    @GetMapping("/resolve-dong") // <--- 새로운 엔드포인트 추가
    public ResponseEntity<KakaoAddressResponse.Document> resolveDongFromCoords(
            @Parameter(description = "조회할 위치의 경도", example = "126.9780")
            @RequestParam(required = true) Double longitude,
            @Parameter(description = "조회할 위치의 위도", example = "37.5665")
            @RequestParam(required = true) Double latitude) {

        KakaoAddressResponse.Document addressDoc = kakaoLocalApiService.getAddressFromCoordinates(longitude, latitude);

        if (addressDoc == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 또는 더 구체적인 오류 메시지
        }
        return ResponseEntity.ok(addressDoc);
    }


    // 1. 게시글 생성 (POST /api/lostposts)
    @Operation(summary = "분실물 게시글 등록", description = "새로운 분실물 게시글을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "게시글 등록 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LostPostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 잘못된 요청", content = @Content)
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LostPostResponseDto> createLostPost(
            @Valid @RequestPart LostPostRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(description = "현재 로그인된 사용자 ID (X-USER-ID 헤더로 전달)", example = "1") // 스웨거 문서화
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // 클라이언트가 X-USER-ID 헤더로 사용자 ID를 보내야 함) {

        LostPostResponseDto createdPost = lostPostService.createLostPost(requestDto, images, userDetails.getMember().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    // 2. 게시글 수정 (PUT /api/lostposts/{lostPostId})
    @Operation(summary = "분실물 게시글 수정", description = "특정 분실물 게시글을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LostPostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 잘못된 요청", content = @Content),
                    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
            })
    @PutMapping(value = "/{lostPostId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LostPostResponseDto> updateLostPost(
            @PathVariable Long lostPostId,
            @Valid @RequestPart LostPostRequestDto requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(description = "현재 로그인된 사용자 ID (X-USER-ID 헤더로 전달)", example = "1") // 스웨거 문서화
            @AuthenticationPrincipal UserDetailsImpl userDetails) { // 클라이언트가 X-USER-ID 헤더로 사용자 ID를 보내야 함) {

        LostPostResponseDto updatedPost = lostPostService.updateLostPost(lostPostId, requestDto, images, userDetails.getMember().getId()); // 수정된 부분
        return ResponseEntity.ok(updatedPost);
    }

    // 3. 게시글 삭제 (DELETE /api/lostposts/{lostPostId})
    @Operation(summary = "분실물 게시글 삭제", description = "특정 분실물 게시글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "게시글 삭제 성공 (반환 내용 없음)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음", content = @Content)
            })
    @DeleteMapping("/{lostPostId}")
    public ResponseEntity<Void> deleteLostPost(@PathVariable Long lostPostId,
                                               @Parameter(description = "현재 로그인된 사용자 ID (X-USER-ID 헤더로 전달)", example = "1") // 스웨거 문서화
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) { // <--- 메서드 파라미터로 memberId를 받습니다.) {
        lostPostService.deleteLostPost(lostPostId, userDetails.getMember().getId());
        return ResponseEntity.noContent().build(); // 204 No Content 응답 (성공적으로 삭제되었지만 반환할 내용이 없음)

    }
    // --- 검색 기능 추가 ---
    @Operation(summary = "분실물 게시글 검색", description = "게시글 제목으로 분실물을 검색합니다. 반환 완료 여부에 따라 필터링할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검색 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleLostPostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (키워드 누락 등)", content = @Content)
            }
    )
    @GetMapping("/search") // 검색 API 엔드포인트
    public ResponseEntity<List<SimpleLostPostResponseDto>> searchLostPosts(
            @Parameter(description = "검색할 키워드 (게시글 제목에 포함)", required = true, example = "아이폰")
            @RequestParam String keyword,
            @Parameter(description = "반환 완료된 게시물 포함 여부 (true: 포함, false: 미포함)", example = "false")
            @RequestParam(defaultValue = "false") boolean includeReturned) {

        List<SimpleLostPostResponseDto> searchResults = lostPostService.searchPosts(keyword, includeReturned);
        return ResponseEntity.ok(searchResults);
    }
}
