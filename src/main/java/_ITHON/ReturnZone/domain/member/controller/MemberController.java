package _ITHON.ReturnZone.domain.member.controller;

import _ITHON.ReturnZone.domain.member.dto.req.LoginRequestDto;
import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto;
import _ITHON.ReturnZone.domain.member.dto.res.SignupResponseDto;
import _ITHON.ReturnZone.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

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