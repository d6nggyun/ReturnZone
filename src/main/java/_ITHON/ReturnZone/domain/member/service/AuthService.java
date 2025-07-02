package _ITHON.ReturnZone.domain.member.service;

import _ITHON.ReturnZone.domain.member.dto.KakaoDTO;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import _ITHON.ReturnZone.global.config.KakaoConfig;
// import _ITHON.ReturnZone.global.util.JwtUtil; // <-- JWT를 사용하지 않으므로 제거
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID; // 임시 비밀번호 생성용

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoConfig kakaoConfig;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        log.info("[카카오 OAuth 로그인 시작] 인가 코드: {}", accessCode);

        // 1. 인가 코드로 카카오 Access Token 요청 (KakaoConfig에서 처리)
        KakaoDTO.OAuthToken oAuthToken = kakaoConfig.requestKakaoAccessToken(accessCode);
        log.info("[카카오 OAuth 로그인] 액세스 토큰 획득 완료");

        // 2. 액세스 토큰으로 사용자 정보 조회 (KakaoConfig에서 처리)
        KakaoDTO.KakaoProfile kakaoProfile = kakaoConfig.requestKakaoUserProfile(oAuthToken.getAccess_token());
        String email = kakaoProfile.getKakao_account().getEmail();
        String nickname = kakaoProfile.getKakao_account().getProfile().getNickname();
        String profileImageUrl = kakaoProfile.getKakao_account().getProfile().getProfileImageUrl();

        log.info("[카카오 OAuth 로그인] 사용자 정보 획득 완료: email={}, nickname={}", email, nickname);

        // 3. 기존 회원인지 확인 후 없으면 회원가입 (Member 엔티티 기준)
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member;

        if (optionalMember.isPresent()) {
            member = optionalMember.get();
            log.info("[카카오 OAuth 로그인] 기존 회원 로그인: memberId={}", member.getId());
        } else {
            // 새 회원가입
            member = createKakaoMember(email, nickname, profileImageUrl);
            log.info("[카카오 OAuth 로그인] 새 회원 가입 완료: memberId={}", member.getId());
        }


        return member;
    }

    // 카카오 로그인으로 회원가입 처리
    @Transactional
    private Member createKakaoMember(String email, String nickname, String profileImageUrl) {
        Member member = new Member();
        member.setEmail(email);
        member.setUsername(nickname);
        member.setProvider("kakao");
        member.setImageUrl(profileImageUrl);

        // 카카오 로그인은 비밀번호가 필요 없지만, DB 스키마에 NOT NULL 제약조건이 있다면 임시 비밀번호를 생성
        member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 무작위 비밀번호 생성 후 암호화

        return memberRepository.save(member);
    }
}