package _ITHON.ReturnZone.domain.member.dto.res;

import _ITHON.ReturnZone.domain.member.entity.BankAccount;
import _ITHON.ReturnZone.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "마이페이지 정보 응답 DTO")
public class MyPageResponseDto {

    @Schema(description = "닉네임", example = "유저1")
    private final String nickname;

    @Schema(description = "프로필 이미지")
    private final String imageUrl;

    @Schema(description = "포인트", example = "120000")
    private final BigDecimal point;

    @Schema(description = "은행", example = "우리은행")
    private final String bankName;

    @Schema(description = "계좌번호", example = "123-456-7890123")
    private final String accountNumber;

    @Schema(description = "입금주 명", example = "홍길동")
    private final String accountHolder;

    @Builder
    private MyPageResponseDto(Member member, BigDecimal point, BankAccount bankAccount) {
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.point = point;
        this.bankName = bankAccount.getBankName();
        this.accountNumber = bankAccount.getAccountNumber();
        this.accountHolder = bankAccount.getAccountHolder();
    }
}
