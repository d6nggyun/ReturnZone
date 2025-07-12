package _ITHON.ReturnZone.domain.member.entity;

import _ITHON.ReturnZone.domain.member.dto.req.UpdateMyPageRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bank_account")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // ex. 국민은행
    @Column(nullable = true)
    private String bankName;

    // ex. 123-456-7890
    @Column(nullable = true)
    private String accountNumber;

    // 예금주 이름
    @Column(nullable = true)
    private String accountHolder;

    @Builder
    private BankAccount(Long memberId, String bankName, String accountNumber, String accountHolder) {
        this.memberId = memberId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
    }

    public void updateBankAccount(UpdateMyPageRequestDto updateMyPageRequestDto) {
        this.bankName = updateMyPageRequestDto.getBankName();
        this.accountNumber = updateMyPageRequestDto.getAccountNumber();
        this.accountHolder = updateMyPageRequestDto.getAccountHolder();
    }
}
