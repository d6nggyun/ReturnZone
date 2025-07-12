package _ITHON.ReturnZone.domain.member.controller;

import _ITHON.ReturnZone.domain.member.dto.req.LoginRequestDto;
import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto;
import _ITHON.ReturnZone.domain.member.dto.res.SignupResponseDto;
import _ITHON.ReturnZone.domain.member.service.MemberService;
import _ITHON.ReturnZone.global.security.jwt.JwtTokenRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "회원 API", description = "회원가입, 로그인 등 회원 관련 API")
@RestController
@RequestMapping("/api/v1/members") // 경로를 /api/v1/members로 통일
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다. (비밀번호는 최소 8자 이상이어야 합니다.)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SignupResponseDto.class))
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(signupRequestDto));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        LoginResponseDto responseDto = memberService.login(loginRequestDto, request); // <-- request 객체 전달
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 재발급 합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "새로운 리프레시 토큰 반환",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "420", description = "만료된 리프레시 토큰입니다."),
                    @ApiResponse(responseCode = "404", description = "리프레시 토큰 조회 실패"),
                    @ApiResponse(responseCode = "400", description = "리프레시 토큰 불일치")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@Valid @RequestBody JwtTokenRequestDto jwtTokenRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.refresh(jwtTokenRequestDto));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용 가능 여부 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "이메일이 중복되었습니다.")
            }
    )
    @GetMapping("/email/{email}")
    public ResponseEntity<Boolean> checkEmailDuplicated(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.checkEmailDuplicated(email));
    }

    // --- 이메일 찾기 API ---
    @Operation(summary = "이메일 찾기 (아이디 찾기)", description = "이름을 통해 가입된 이메일 주소를 찾습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일 주소 반환",
                            content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "400", description = "정보 불일치 또는 잘못된 요청", content = @Content)
            }
    )
    @GetMapping("/find-email")
    public ResponseEntity<String> findEmail(
            @Parameter(description = "사용자 닉네임", example = "홍길동", required = true) @RequestParam String nickname) {
        try {
            String email = memberService.findMemberEmail(nickname);
            return ResponseEntity.ok(email);
        } catch (UnsupportedOperationException e) { // <-- UnsupportedOperationException 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- 비밀번호 찾기 API ---

    // 1단계: 사용자 이메일과 이름 일치 여부 확인
    @Operation(summary = "비밀번호 찾기 1단계: 사용자 확인", description = "비밀번호 재설정 전 이메일과 이름으로 사용자 존재 여부를 확인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 정보 일치",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "사용자 정보 불일치 또는 잘못된 요청", content = @Content)
            }
    )
    @PostMapping("/find-password/check")
    public ResponseEntity<Boolean> checkUserForPasswordReset(
            @Parameter(description = "사용자 이메일", example = "test@example.com", required = true) @RequestParam String email,
            @Parameter(description = "사용자 닉네임", example = "MANGO1", required = true) @RequestParam String nickname) { // <-- 파라미터 변경
        boolean exists = memberService.checkMemberExistenceForPasswordReset(email, nickname); // <-- 메서드 호출 변경
        if (exists) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    // 2단계: 임시 비밀번호 발송 및 DB 업데이트
    @Operation(summary = "비밀번호 찾기 2단계: 임시 비밀번호 발송", description = "확인된 사용자 이메일로 임시 비밀번호를 발송하고 DB에 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "임시 비밀번호 발송 성공", content = @Content),
                    @ApiResponse(responseCode = "400", description = "사용자 정보 불일치 또는 이메일 발송 실패", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 (메일 서버 문제 등)", content = @Content)
            }
    )
    @PostMapping("/find-password/send")
    public ResponseEntity<String> sendTemporaryPassword(
            @Parameter(description = "사용자 이메일", example = "test@example.com", required = true) @RequestParam String email,
            @Parameter(description = "사용자 닉네임", example = "MANGO1", required = true) @RequestParam String nickname) { // <-- 파라미터 변경
        try {
            memberService.sendTemporaryPasswordAndReset(email, nickname); // <-- 메서드 호출 변경
            return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("임시 비밀번호 발송 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("임시 비밀번호 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 세션을 무효화하여 로그아웃합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "400", description = "로그인되지 않은 상태")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션이 없으면 null 반환
        if (session != null) {
            session.invalidate(); // 세션 무효화
            log.info("로그아웃 성공: sessionId={}", session.getId());
            return ResponseEntity.ok("로그아웃 되었습니다.");
        }
        return ResponseEntity.badRequest().body("로그인된 상태가 아닙니다.");
    }
}