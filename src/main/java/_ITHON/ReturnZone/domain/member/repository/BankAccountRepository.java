package _ITHON.ReturnZone.domain.member.repository;

import _ITHON.ReturnZone.domain.member.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByMemberId(Long memberId);
}
