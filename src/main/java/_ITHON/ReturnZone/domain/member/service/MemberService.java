package _ITHON.ReturnZone.domain.member.service;

import _ITHON.ReturnZone.domain.member.dto.req.LoginRequestDto;
import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto;
import _ITHON.ReturnZone.domain.member.dto.res.SignupResponseDto;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import _ITHON.ReturnZone.global.security.jwt.JwtTokenProvider;
import _ITHON.ReturnZone.global.security.jwt.JwtTokenRequestDto;
import _ITHON.ReturnZone.global.security.jwt.RefreshToken;
import _ITHON.ReturnZone.global.security.jwt.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        log.info("[회원가입 요청] email={}", signupRequestDto.getEmail());

        if (memberRepository.existsByEmail(signupRequestDto.getEmail())) {
            log.warn("[이미 가입된 이메일] email={}", signupRequestDto.getEmail());
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        Member member = new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()));
        memberRepository.save(member);

        log.info("[회원가입 완료] memberId={}, email={}", member.getId(), member.getEmail());
        return SignupResponseDto.builder().member(member).build();
    }

    @Transactional // 로그인 기능은 데이터 변경이 없으므로 readOnly = true
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        log.info("[로그인 요청] email={}", loginRequestDto.getEmail());

        // 1. 이메일로 사용자 찾기
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 이메일: {}", loginRequestDto.getEmail());
                    return new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
                });

        // 2. 비밀번호 일치 여부 확인 (암호화된 비밀번호와 비교)
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            log.warn("[로그인 실패] 비밀번호 불일치: email={}", loginRequestDto.getEmail());
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken = loginRequestDto.toAuthenticationToken();
        log.debug("Spring Security 인증 시작");

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        if (!authentication.isAuthenticated()) {
            log.error("[로그인 실패] 인증 실패: email={}", loginRequestDto.getEmail());
            throw new RuntimeException("로그인 인증에 실패했습니다.");
        }

        if (refreshTokenRepository.findByEmail(member.getEmail()).isPresent()) {
            refreshTokenRepository.deleteByEmail(member.getEmail());
        }

        // JWT 토큰 생성
        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[JWT 발급 완료] memberId={}, email={}", member.getId(), member.getEmail());

        RefreshToken refreshToken = RefreshToken.builder().member(member).refreshToken(loginResponseDto.getRefreshToken()).build();
        refreshTokenRepository.save(refreshToken);

        HttpSession session = request.getSession();
        session.setAttribute("loggedInMemberId", member.getId());
        session.setMaxInactiveInterval(30 * 60);
        log.info("[로그인 성공] memberId={}, email={}, sessionId={}", member.getId(), member.getEmail(), session.getId());

        return loginResponseDto;
    }

    @Transactional
    public LoginResponseDto refresh(JwtTokenRequestDto jwtTokenRequestDto) {

        log.info("[JWT 토큰 재발급 요청]");

        if (!jwtTokenProvider.validateToken(jwtTokenRequestDto.getRefreshToken(), "refresh")) {
            log.warn("[JWT 토큰 유효성 검증 실패] 만료된 Refresh Token");
            throw new RuntimeException("만료된 Refresh Token입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(jwtTokenRequestDto.getRefreshToken());
        log.info("[인증 정보 추출 완료] username={}", authentication.getName());

        String email = authentication.getName();

        log.info("[RefreshToken 조회 시도] email={}", email);
        RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[RefreshToken 조회 실패] 저장된 토큰 없음 (username={})", email);
                    return new RuntimeException("저장된 RefreshToken이 없습니다.");
                });

        if (!refreshToken.getRefreshToken().equals(jwtTokenRequestDto.getRefreshToken())) {
            log.warn("[RefreshToken 불일치] 요청 토큰과 저장 토큰이 다름 (username={})", email);
            throw new RuntimeException("RefreshToken이 일치하지 않습니다.");
        }

        log.info("[RefreshToken 일치 확인 완료] 새 AccessToken 및 RefreshToken 생성 시작");
        LoginResponseDto loginResponseDto = jwtTokenProvider.generateToken(authentication);
        log.info("[AccessToken/RefreshToken 재발급 완료] username={}", email);

        RefreshToken newRefreshToken = refreshToken.updateValue(loginResponseDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);
        log.info("[새 RefreshToken 저장 완료] email={}", email);

        return loginResponseDto;
    }

    @Transactional(readOnly = true)
    public Boolean checkEmailDuplicated(String email) {

        log.info("[이메일 중복 확인] email={}", email);

        if (memberRepository.existsByEmail(email)) {
            log.warn("[중복 이메일] email={}", email);
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        return true;
    }
}