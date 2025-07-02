package _ITHON.ReturnZone.domain.lostpost.controller;

import _ITHON.ReturnZone.domain.lostpost.dto.res.LostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.dto.res.SimpleLostPostResponseDto;
import _ITHON.ReturnZone.domain.lostpost.entity.SortType;
import _ITHON.ReturnZone.domain.lostpost.service.LostPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lostPosts")
@Tag(name = "LostPost", description = "분실물 관련 API")
public class LostPostController {

    private final LostPostService lostPostService;

    @Operation(summary = "분실물 목록 조회", description = "분실물 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "분실물 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleLostPostResponseDto.class)))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<SimpleLostPostResponseDto>> getLostPostList(
            @RequestParam(defaultValue = "LATEST") SortType sort,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Boolean instant,
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(lostPostService.getLostPostList(sort, latitude, longitude, instant, category, pageable));
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
}
