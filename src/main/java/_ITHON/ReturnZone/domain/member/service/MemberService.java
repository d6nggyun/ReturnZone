package _ITHON.ReturnZone.domain.member.service;

import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
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

        Member member = new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()));

        memberRepository.save(member);

        log.info("[회원가입 완료] memberId={}, email={}", member.getId(), member.getEmail());

        return SignupResponseDto.builder().member(member).build();
    }
}
