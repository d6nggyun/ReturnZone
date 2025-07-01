package _ITHON.ReturnZone.domain.member.entity;

import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false) // provider 필드를 추가하고, DB에서 NOT NULL 제약조건이 있다면 nullable = false로 설정
    private String provider; // 어떤 방식으로 가입했는지 (예: "local", "kakao", "google")

    public Member(SignupRequestDto signupRequestDto, String encodedPassword) {
        this.email = signupRequestDto.getEmail();
        this.username = signupRequestDto.getUsername();
        this.password = encodedPassword;
        this.provider = "local"; // <-- 회원가입 시 기본값을 "local"로 설정
    }
}