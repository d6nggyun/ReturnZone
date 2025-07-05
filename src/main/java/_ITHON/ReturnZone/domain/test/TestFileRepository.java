package _ITHON.ReturnZone.domain.test; // 패키지 변경

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestFileRepository extends JpaRepository<TestFileEntity, Long> {
    // TestFileEntity가 같은 패키지 내에 있으므로 import가 필요 없습니다.
}