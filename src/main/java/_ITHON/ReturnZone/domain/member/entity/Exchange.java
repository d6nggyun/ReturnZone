package _ITHON.ReturnZone.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exchange")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long bankAccountId;

    @Column(nullable = false)
    private BigDecimal point;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status;

    private String adminMemo;

    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @Builder
    private Exchange(Member member, Long bankAccountId, ExchangeStatus status, String adminMemo) {
        this.memberId = member.getId();
        this.bankAccountId = bankAccountId;
        this.point = member.getPoint();
        this.status = status;
        this.adminMemo = adminMemo;
        this.requestedAt = LocalDateTime.now();
        this.processedAt = null;
    }

    public void approve(String memo) {
        this.status = ExchangeStatus.APPROVED;
        this.adminMemo = memo;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String memo) {
        this.status = ExchangeStatus.REJECTED;
        this.adminMemo = memo;
        this.processedAt = LocalDateTime.now();
    }
}
