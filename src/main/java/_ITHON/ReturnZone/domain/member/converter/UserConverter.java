package _ITHON.ReturnZone.domain.member.converter;

import _ITHON.ReturnZone.domain.member.dto.res.LoginResponseDto; // LoginResponseDto 임포트
import _ITHON.ReturnZone.domain.member.entity.Member; // Member 엔티티 임포트
import org.springframework.stereotype.Component; // 스프링 빈으로 등록하기 위한 어노테이션

@Component // 스프링 빈으로 등록하여 다른 클래스에서 주입받아 사용할 수 있도록 함
public class UserConverter {

    /**
     * Member 엔티티를 LoginResponseDto로 변환합니다.
     * 이 메서드는 정적(static)으로 선언하여 인스턴스 생성 없이 사용할 수 있도록 하거나,
     * 또는 @Component로 빈 등록 후 주입받아 사용할 수 있습니다.
     * 여기서는 @Component로 등록하고 static 메서드로도 제공하여 유연성을 높였습니다.
     */
    public static LoginResponseDto toLoginResponseDto(Member member) {
        // LoginResponseDto의 builder를 사용하여 Member의 email, username, imageUrl을 매핑
        // LoginResponseDto의 builder가 member를 직접 받아서 생성하는 방식이라고 가정합니다.
        // 만약 LoginResponseDto.builder().email(member.getEmail()).username(member.getUsername()).build()
        // 와 같이 각 필드를 매핑해야 한다면 해당 방식으로 수정해야 합니다.
        return LoginResponseDto.builder()
                .member(member) // LoginResponseDto의 builder가 Member 객체를 인자로 받는다고 가정
                .build();
    }

    // 필요하다면 다른 변환 메서드들도 여기에 추가할 수 있습니다.
    // 예: toUserResponseDto(Member member), toSignupResponseDto(Member member) 등
}