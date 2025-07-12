package _ITHON.ReturnZone.domain.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import _ITHON.ReturnZone.domain.member.entity.Member; // Member 엔티티 import

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "현상금 지급 및 관리자 포인트 충전 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "현상금 지급", description = "거래 완료 시 게시글 작성자가 채팅 상대에게 현상금을 지급합니다. 지급자의 총 포인트에서 차감되고 수령자의 총 포인트 및 환전 가능 포인트에 추가됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현상금 지급 성공", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "포인트 부족 또는 회원 정보 오류", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/pay-reward")
    public ResponseEntity<String> payReward(
            @Parameter(description = "현상금 지급자 회원 ID", example = "1", required = true)
            @AuthenticationPrincipal Member member, // Member 엔티티를 @AuthenticationPrincipal로 받는 경우
            @Parameter(description = "현상금 수령자 회원 ID", example = "2", required = true)
            @RequestParam Long receiverId,
            @Parameter(description = "지급할 현상금 포인트", example = "10000.00", required = true)
            @RequestParam BigDecimal rewardAmount) {
        try {
            paymentService.payReward(member.getId(), receiverId, rewardAmount);
            return ResponseEntity.ok("현상금 지급 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "관리자 포인트 충전", description = "관리자가 특정 회원에게 직접 포인트를 충전합니다. 총 포인트와 환전 가능 포인트에 모두 추가됩니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "포인트 충전 성공", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "회원 정보 오류", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/add-points")
    public ResponseEntity<String> addPoints(
            @Parameter(description = "포인트를 충전할 회원 ID", example = "3", required = true)
            @RequestParam Long memberId,
            @Parameter(description = "충전할 포인트 수량", example = "50000.00", required = true)
            @RequestParam BigDecimal points) {
        try {
            paymentService.addPoints(memberId, points);
            return ResponseEntity.ok("포인트 충전 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "게시글 현상금 조회", description = "특정 게시글에 설정된 현상금 금액을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현상금 조회 성공", content = @Content(schema = @Schema(implementation = BigDecimal.class))),
                    @ApiResponse(responseCode = "400", description = "게시글을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @GetMapping("/reward/{postId}")
    public ResponseEntity<?> getReward(
            @Parameter(description = "현상금을 조회할 게시글 ID", example = "1", required = true)
            @PathVariable Long postId) {
        try {
            BigDecimal reward = paymentService.getRewardByPostId(postId);
            return ResponseEntity.ok(reward);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "게시글 현상금 수정", description = "특정 게시글에 설정된 현상금 금액을 수정합니다. (게시글 작성자만 가능하도록 서비스에서 검증 필요)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현상금 수정 완료", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "게시글을 찾을 수 없거나 권한이 없습니다.", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PutMapping("/reward/{postId}")
    public ResponseEntity<String> updateReward(
            @Parameter(description = "현상금을 수정할 게시글 ID", example = "1", required = true)
            @PathVariable Long postId,
            @Parameter(description = "새로운 현상금 정보", required = true)
            @Valid @RequestBody RewardDto rewardDto) {
        try {
            paymentService.updateReward(postId, rewardDto.getReward());
            return ResponseEntity.ok("현상금 수정 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}