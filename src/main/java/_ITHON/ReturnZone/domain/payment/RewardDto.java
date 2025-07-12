package _ITHON.ReturnZone.domain.payment; // PaymentService와 동일한 패키지 또는 별도 DTO 패키지

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardDto {

    @NotNull(message = "현상금은 필수입니다.")
    @DecimalMin(value = "0.00", message = "현상금은 0원 이상이어야 합니다.")
    private BigDecimal reward;
}