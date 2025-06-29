package _ITHON.ReturnZone.domain.member.entity;

import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public Member(SignupRequestDto signupRequestDto, String encodedPassword) {
        this.email = signupRequestDto.getEmail();
        this.username = signupRequestDto.getUsername();
        this.password = encodedPassword;
    }
}
