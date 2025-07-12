package _ITHON.ReturnZone.domain.member.dto.res;

import _ITHON.ReturnZone.domain.member.entity.Exchange;
import _ITHON.ReturnZone.domain.member.entity.ExchangeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Schema(description = "환전 내역 응답 DTO")
public class ExchangeResponseDto {

    @Schema(description = "환전 ID", example = "1")
    private final Long id;

    @Schema(description = "포인트", example = "10000")
    private final BigDecimal point;

    @Schema(description = "환전 상태", example = "APPROVED")
    private final ExchangeStatus status;

    @Schema(description = "관리자 메모", example = "환전 승인됨")
    private final String adminMemo;

    @Schema(description = "환전 요청 시간", example = "2025-07-09T14:30:00")
    private final LocalDateTime requestedAt;

    @Schema(description = "처리 완료 시간", example = "2025-07-10T09:00:00")
    private final LocalDateTime processedAt;

    @Builder
    private ExchangeResponseDto(Exchange exchange) {
        this.id = exchange.getId();
        this.point = exchange.getPoint();
        this.status = exchange.getStatus();
        this.adminMemo = exchange.getAdminMemo();
        this.requestedAt = exchange.getRequestedAt();
        this.processedAt = exchange.getProcessedAt();
    }
}
