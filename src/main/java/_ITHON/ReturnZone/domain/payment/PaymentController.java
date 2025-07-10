package _ITHON.ReturnZone.domain.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "현상금 지급 및 관리자 포인트 충전 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "현상금 지급", description = "거래 완료 시 게시글 작성자가 채팅 상대에게 현상금을 지급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현상금 지급 성공", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "포인트 부족 또는 회원 정보 오류", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/pay-reward")
    public ResponseEntity<String> payReward(
            @Parameter(description = "현상금 지급자 회원 ID", example = "1", required = true)
            @RequestParam Long payerId,
            @Parameter(description = "현상금 수령자 회원 ID", example = "2", required = true)
            @RequestParam Long receiverId,
            @Parameter(description = "지급할 현상금 포인트", example = "10000", required = true)
            @RequestParam int rewardAmount) {
        try {
            paymentService.payReward(payerId, receiverId, rewardAmount);
            return ResponseEntity.ok("현상금 지급 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "관리자 포인트 충전", description = "관리자가 회원에게 직접 포인트를 충전합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "포인트 충전 성공", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "회원 정보 오류", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/add-points")
    public ResponseEntity<String> addPoints(
            @Parameter(description = "포인트를 충전할 회원 ID", example = "3", required = true)
            @RequestParam Long memberId,
            @Parameter(description = "충전할 포인트 수량", example = "10000", required = true)
            @RequestParam int points) {
        try {
            paymentService.addPoints(memberId, points);
            return ResponseEntity.ok("포인트 충전 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}