package _ITHON.ReturnZone.domain.lostpost.repository;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostPostRepository extends JpaRepository<LostPost, Long> {
}
