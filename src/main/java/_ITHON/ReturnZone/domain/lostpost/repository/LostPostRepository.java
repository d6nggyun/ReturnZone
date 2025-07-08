package _ITHON.ReturnZone.domain.lostpost.repository;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.entity.RegistrationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LostPostRepository extends JpaRepository<LostPost, Long> {

    // 반환 완료 안 된 게시물 중 제목에 키워드 포함된 게시물 조회
    @Query("SELECT l FROM LostPost l WHERE l.title LIKE %:keyword% AND l.isReturned = false")
    List<LostPost> findByTitleContainingAndNotReturned(@Param("keyword") String keyword);

    // 반환 완료 여부 상관없이 제목에 키워드 포함된 게시물 조회
    @Query("SELECT l FROM LostPost l WHERE l.title LIKE %:keyword%")
    List<LostPost> findByTitleContaining(@Param("keyword") String keyword);

    @Query(value = """
    SELECT lp.*, 
            (6371000 * acos(
            cos(radians(:lat)) * cos(radians(lp.latitude))
            * cos(radians(lp.longitude) - radians(:lng))
            + sin(radians(:lat)) * sin(radians(lp.latitude))
            )) AS distance
    FROM lost_post lp
    WHERE (:category IS NULL OR lp.category = :category)
        AND (:instant IS NULL OR lp.instant_settlement = :instant)
        AND (:type IS NULL OR lp.registration_type = :type)
    ORDER BY distance ASC
    """,
            countQuery = """
    SELECT count(*) FROM lost_post lp
    WHERE (:category IS NULL OR lp.category = :category)
        AND (:instant IS NULL OR lp.instant_settlement = :instant)
        AND (:type IS NULL OR lp.registration_type = :type)
    """,
            nativeQuery = true)
    Slice<LostPost> findByFilterOrderByDistance(@Param("type") String type,
                                                @Param("lat") double lat,
                                                @Param("lng") double lng,
                                                @Param("category") String category,
                                                @Param("instant") Boolean instant,
                                                Pageable pageable);

    @Query("""
        SELECT lp
        FROM LostPost lp
        WHERE (:category IS NULL OR lp.category = :category)
          AND (:instant  IS NULL OR lp.instantSettlement = :instant)
          AND (:type     IS NULL OR lp.registrationType = :type)
        """)
    Slice<LostPost> findByFilter(@Param("type") RegistrationType type,
                                 @Param("category") String category,
                                 @Param("instant") Boolean instant,
                                 Pageable pageable);

    @Query("""
        SELECT lp
        FROM   LostPost lp
        WHERE  lp.category = :category
            AND  lp.isReturned = false
    """)
    Slice<LostPost> findTop50ByCategoryAndIsReturnedFalse(@Param("category") String category, Pageable pageable);
}
