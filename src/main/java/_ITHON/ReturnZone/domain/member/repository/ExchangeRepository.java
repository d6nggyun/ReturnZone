package _ITHON.ReturnZone.domain.member.repository;

import _ITHON.ReturnZone.domain.member.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange,Long> {

    List<Exchange> findByMemberIdOrderByRequestedAtDesc(Long memberId);
}
