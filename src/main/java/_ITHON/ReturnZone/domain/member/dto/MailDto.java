package _ITHON.ReturnZone.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailDto {
    private String address; // 받는 사람 이메일 주소
    private String title;   // 메일 제목
    private String message; // 메일 내용
}