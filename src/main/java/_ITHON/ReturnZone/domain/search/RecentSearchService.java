package _ITHON.ReturnZone.domain.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;

    @Transactional
    public void addRecentSearch(Long memberId, String keyword) {
        if (!recentSearchRepository.existsByMemberIdAndKeyword(memberId, keyword)) {
            recentSearchRepository.save(new RecentSearch(memberId, keyword, LocalDateTime.now()));
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRecentSearches(Long memberId) {
        return recentSearchRepository.findByMemberIdOrderBySearchedAtDesc(memberId)
                .stream()
                .map(RecentSearch::getKeyword)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRecentSearch(Long memberId, String keyword) {
        recentSearchRepository.deleteByMemberIdAndKeyword(memberId, keyword);
    }

    @Transactional
    public void deleteAllRecentSearches(Long memberId) {
        recentSearchRepository.deleteByMemberId(memberId);
    }
}