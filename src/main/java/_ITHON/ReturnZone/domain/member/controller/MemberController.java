package _ITHON.ReturnZone.domain.member.controller;

import _ITHON.ReturnZone.domain.member.dto.req.SignupRequestDto;
import _ITHON.ReturnZone.domain.member.dto.res.SignupResponseDto;
import _ITHON.ReturnZone.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "Member", description = "회원 관련 API")
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
}
