package _ITHON.ReturnZone.domain.member.repository;

import _ITHON.ReturnZone.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    // 이메일과 이름으로 회원 찾기 (비밀번호 찾기 1단계: 사용자 확인용)
    Optional<Member> findByEmailAndNickname(String email, String nickname);

    List<Member> findByNickname(String nickname);

    // 회원 비밀번호 업데이트
    @Modifying
    @Query("UPDATE Member m SET m.password = :password WHERE m.id = :id")
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);
}

