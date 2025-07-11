package _ITHON.ReturnZone.domain.search;

import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.service.LostPostService;
import _ITHON.ReturnZone.global.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema; // ArraySchema import
import io.swagger.v3.oas.annotations.media.Content; // Content import
import io.swagger.v3.oas.annotations.media.Schema; // Schema import
import io.swagger.v3.oas.annotations.responses.ApiResponse; // ApiResponse import
import io.swagger.v3.oas.annotations.tags.Tag; // Tag import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search") // 검색 관련 API는 이 경로 아래에 둡니다.
@RequiredArgsConstructor
@Tag(name = "Search", description = "검색 및 최근 검색어 관련 API") // 스웨거 태그
public class SearchController {

    private final RecentSearchService recentSearchService;
    private final LostPostService lostPostService; // LostPostService 주입 (검색 시 최근 검색어 추가용)

    // 최근 검색어 조회
    @Operation(summary = "최근 검색어 조회", description = "현재 로그인된 사용자의 최근 검색어 목록을 최신순으로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = String.class)))), // String 배열 스키마
                    @ApiResponse(responseCode = "401", description = "인증 실패 (X-USER-ID 누락/잘못됨)", content = @Content)
            }
    )
    @GetMapping("/recent")
    public ResponseEntity<List<String>> getRecentSearches(
            @Parameter(description = "현재 로그인된 사용자 ID (X-USER-ID 헤더로 전달)", example = "1", required = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<String> recentSearches = recentSearchService.getRecentSearches(userDetails.getMember().getId());
        return ResponseEntity.ok(recentSearches);
    }

    // 최근 검색어 개별 삭제
    @Operation(summary = "최근 검색어 개별 삭제", description = "현재 로그인된 사용자의 특정 최근 검색어를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (keyword 누락)", content = @Content)
            }
    )
    @DeleteMapping("/recent")
    public ResponseEntity<Void> deleteRecentSearch(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "삭제할 검색어", required = true, example = "아이폰")
            @RequestParam String keyword) {
        recentSearchService.deleteRecentSearch(userDetails.getMember().getId(), keyword);
        return ResponseEntity.noContent().build();
    }

    // 최근 검색어 전체 삭제
    @Operation(summary = "최근 검색어 전체 삭제", description = "현재 로그인된 사용자의 모든 최근 검색어를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
            }
    )
    @DeleteMapping("/recent/all")
    public ResponseEntity<Void> deleteAllRecentSearches(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        recentSearchService.deleteAllRecentSearches(userDetails.getMember().getId());
        return ResponseEntity.noContent().build();
    }

    // 게시물 제목 검색
    @Operation(summary = "분실물 게시글 검색", description = "게시글 제목으로 분실물을 검색합니다. 반환 완료 여부에 따라 필터링할 수 있습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "검색 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleLostPostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (키워드 누락 등)", content = @Content)
            }
    )
    @GetMapping("/posts") // 검색 API 엔드포인트
    public ResponseEntity<List<SimpleLostPostResponseDto>> searchPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Parameter(description = "검색할 키워드 (게시글 제목에 포함)", required = true, example = "아이폰")
            @RequestParam String keyword,
            @Parameter(description = "반환 완료된 게시물 포함 여부 (true: 포함, false: 미포함)", example = "false")
            @RequestParam(defaultValue = "false") boolean includeReturned) {

        // 최근 검색어에 자동 추가
        recentSearchService.addRecentSearch(userDetails.getMember().getId(), keyword);

        List<SimpleLostPostResponseDto> searchResults = lostPostService.searchPosts(keyword, includeReturned);
        return ResponseEntity.ok(searchResults);
    }
}