package _ITHON.ReturnZone.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bank_account")
public class BankAccount {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // ex. 국민은행
    @Column(nullable = false)
    private String bankName;

    // ex. 123-456-7890
    @Column(nullable = false)
    private String accountNumber;

    // 예금주 이름
    @Column(nullable = false)
    private String accountHolder;
}
