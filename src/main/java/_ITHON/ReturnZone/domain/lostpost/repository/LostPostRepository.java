package _ITHON.ReturnZone.domain.lostpost.repository;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LostPostRepository extends JpaRepository<LostPost, Long> {

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
    ORDER BY distance ASC
    """,
            countQuery = """
    SELECT count(*) FROM lost_post lp
    WHERE (:category IS NULL OR lp.category = :category)
        AND (:instant IS NULL OR lp.instant_settlement = :instant)
    """,
            nativeQuery = true)
    Page<LostPost> findByFilterOrderByDistance(@Param("lat") double lat,
                                               @Param("lng") double lng,
                                               @Param("category") String category,
                                               @Param("instant") Boolean instant,
                                               Pageable pageable);

    @Query("""
    SELECT lp FROM LostPost lp
    WHERE (:category IS NULL OR lp.category = :category)
        AND (:instant IS NULL OR lp.instantSettlement = :instant)
    """)
    Page<LostPost> findByFilter(String category, Boolean instant, Pageable pageable);
}
