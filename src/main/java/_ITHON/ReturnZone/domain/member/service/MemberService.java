package _ITHON.ReturnZone.domain.member.service;

import _ITHON.ReturnZone.domain.member.dto.req.LoginRequestDto; // 추가
import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto; // 추가
import _ITHON.ReturnZone.domain.member.dto.res.SignupResponseDto;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Transactional(readOnly = true) // 로그인 기능은 데이터 변경이 없으므로 readOnly = true
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
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

        log.info("[로그인 성공] memberId={}, email={}", member.getId(), member.getEmail());
        return LoginResponseDto.builder().member(member).build();
    }
}