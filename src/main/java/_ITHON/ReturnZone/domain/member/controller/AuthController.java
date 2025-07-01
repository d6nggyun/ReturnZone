package _ITHON.ReturnZone.domain.member.controller;

import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto;
import _ITHON.ReturnZone.domain.member.entity.Member;
import _ITHON.ReturnZone.domain.member.response.KakaoResponse;
import _ITHON.ReturnZone.domain.member.service.AuthService;
import _ITHON.ReturnZone.domain.member.response.KakaoResponse;
import _ITHON.ReturnZone.domain.member.converter.UserConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "인증 API", description = "카카오 로그인 등 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "카카오 로그인 콜백", description = "카카오 인가 코드를 받아 로그인/회원가입을 처리합니다.")
    @GetMapping("/kakao")
    public KakaoResponse<LoginResponseDto> kakaoLogin(
            @RequestParam("code") String code,
            HttpServletResponse response) {

        Member member = authService.oAuthLogin(code, response);
        LoginResponseDto loginResponseDto = UserConverter.toLoginResponseDto(member);

        return KakaoResponse.onSuccess(loginResponseDto);
    }
}