package _ITHON.ReturnZone.domain.member.repository;

import _ITHON.ReturnZone.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
