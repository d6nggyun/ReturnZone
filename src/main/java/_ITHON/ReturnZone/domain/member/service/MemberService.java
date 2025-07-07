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
import _ITHON.ReturnZone.domain.member.dto.MailDto;
import _ITHON.ReturnZone.global.util.EncryptionUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.time.LocalDateTime; // LocalDateTime import
import java.util.Optional;
import java.util.Random; // Random import

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender; //이메일 발송용

    // TODO: application.yml의 username과 일치해야 합니다.
    private static final String FROM_ADDRESS = "qwemnb3013@gmail.com"; // <-- 여기에 실제 발신자 이메일 주소를 입력하세요!

    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        log.info("[회원가입 요청] email={}", signupRequestDto.getEmail());

        if (memberRepository.existsByEmail(signupRequestDto.getEmail())) {
            log.warn("[이미 가입된 이메일] email={}", signupRequestDto.getEmail());
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // MD5로 비밀번호 암호화하여 저장
        String encryptedPassword = EncryptionUtil.encryptMD5(signupRequestDto.getPassword());
        if (encryptedPassword == null) {
            throw new RuntimeException("비밀번호 암호화에 실패했습니다.");
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

    @Transactional(readOnly = true)
    public Boolean checkEmailDuplicated(String email) {

        log.info("[이메일 중복 확인] email={}", email);

        if (memberRepository.existsByEmail(email)) {
            log.warn("[중복 이메일] email={}", email);
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }
        return true;
    }

    @Transactional(readOnly = true)
    public String findMemberEmail(String nickname) { // 전화번호 제외, 이름만으로 찾는 경우 (권장하지 않음)
        List<Member> members = memberRepository.findByNickname(nickname); // <-- 닉네임으로 조회

        if (members.isEmpty()) {
            log.warn("[이메일 찾기 실패] 일치하는 닉네임 없음: {}", nickname);
            throw new IllegalArgumentException("입력하신 닉네임과 일치하는 이메일이 없습니다.");
        }
        if (members.size() > 1) {
            log.warn("[이메일 찾기 실패] 동일 닉네임 다수 존재: {}", nickname);
            throw new IllegalArgumentException("동일한 닉네임이 다수 존재하여 이메일을 찾을 수 없습니다. 다른 정보로 시도해주세요.");
        }

        // 닉네임이 고유하고, 정확히 한 명의 회원만 조회된 경우
        return members.get(0).getEmail();
    }

    // --- 비밀번호 찾기 기능 ---

    // 1단계: 이메일과 이름으로 사용자 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean checkMemberExistenceForPasswordReset(String email, String nickname) {
        return memberRepository.findByEmailAndNickname(email, nickname).isPresent();
    }

    // 2단계: 임시 비밀번호 생성, DB 업데이트, 메일 발송
    @Transactional
    public void sendTemporaryPasswordAndReset(String email, String nickname) {
        Optional<Member> memberOptional = memberRepository.findByEmailAndNickname(email, nickname);
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("입력하신 정보와 일치하는 사용자가 없습니다.");
        }

        Member member = memberOptional.get();
        String tempPassword = generateTemporaryPassword(); // 임시 비밀번호 생성

        // MD5로 임시 비밀번호 암호화
        String encryptedPassword = EncryptionUtil.encryptMD5(tempPassword);
        if (encryptedPassword == null) {
            throw new RuntimeException("임시 비밀번호 암호화에 실패했습니다.");
        }

        // DB에 임시 비밀번호로 업데이트
        memberRepository.updatePasswordById(member.getId(), encryptedPassword);
        log.info("회원 ID {}의 비밀번호가 임시 비밀번호로 업데이트되었습니다.", member.getId());

        // 메일 DTO 생성 (메일에는 암호화되지 않은 임시 비밀번호를 보냅니다)
        MailDto mailDto = new MailDto();
        mailDto.setAddress(member.getEmail());
        mailDto.setTitle(member.getNickname() + "님의 ReturnZone 임시 비밀번호 안내 이메일입니다.");
        mailDto.setMessage("안녕하세요. ReturnZone 임시 비밀번호 안내 관련 이메일입니다.\n" +
                member.getNickname() + "님의 임시 비밀번호는 **" + tempPassword + "** 입니다.\n" +
                "로그인 후 반드시 비밀번호를 변경해 주세요.");

        // 메일 발송
        sendMail(mailDto);
    }

    // 임시 비밀번호 생성 (10자리 랜덤 문자열)
    private String generateTemporaryPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z' };
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(charSet[random.nextInt(charSet.length)]);
        }
        return sb.toString();
    }

    // 이메일 발송
    private void sendMail(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(mailDto.getTitle());
        message.setText(mailDto.getMessage());
        mailSender.send(message);
        log.info("이메일 전송 완료! 수신자: {}", mailDto.getAddress());
    }
}