package _ITHON.ReturnZone.domain.lostpost.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegistrationType {
    LOST("분실했어요"),
    FOUND("주인을 찾아요");

    private final String description; // 스웨거 등에서 보여줄 설명
}