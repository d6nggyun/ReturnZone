package _ITHON.ReturnZone.domain.search;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;          // 사용자 ID

    private String keyword;         // 검색어

    private LocalDateTime searchedAt;  // 검색한 시간

    public RecentSearch(Long memberId, String keyword, LocalDateTime searchedAt) {
        this.memberId = memberId;
        this.keyword = keyword;
        this.searchedAt = searchedAt;
    }
}