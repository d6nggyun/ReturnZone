package _ITHON.ReturnZone.domain.search;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

    List<RecentSearch> findByMemberIdOrderBySearchedAtDesc(Long memberId);

    void deleteByMemberIdAndKeyword(Long memberId, String keyword);

    void deleteByMemberId(Long memberId);

    boolean existsByMemberIdAndKeyword(Long memberId, String keyword);
}