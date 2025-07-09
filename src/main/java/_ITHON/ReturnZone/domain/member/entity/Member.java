package _ITHON.ReturnZone.domain.member.entity;

import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.req.UpdateMyPageRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Column(nullable = true)
    private String location;

    @Column(nullable = true)
    private String locationDetail;

    @Column(nullable = false)
    private BigDecimal point = BigDecimal.ZERO;

    public Member(SignupRequestDto signupRequestDto, String encodedPassword) {
        this.email = signupRequestDto.getEmail();
        this.username = signupRequestDto.getUsername();
        this.nickname = signupRequestDto.getUsername();
        this.password = encodedPassword;
        this.provider = "local"; // <-- 회원가입 시 기본값을 "local"로 설정
    }

    public void updateMyPage(UpdateMyPageRequestDto updateMyPageRequestDto, String encodedPassword, String imageUrl) {
        this.nickname = updateMyPageRequestDto.getNickname();
        this.password = encodedPassword;
        this.imageUrl = imageUrl != null ? imageUrl : this.imageUrl;
        this.location = updateMyPageRequestDto.getLocation();
        this.locationDetail = updateMyPageRequestDto.getLocationDetail();
    }

    public void usePoint(BigDecimal point) {
        this.point = this.point.subtract(point);
    }

    public void refundPoint(BigDecimal point) {
        this.point = this.point.add(point);
    }
}