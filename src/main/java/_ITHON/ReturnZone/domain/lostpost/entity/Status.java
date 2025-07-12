package _ITHON.ReturnZone.domain.lostpost.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    FINDING_OWNER("주인 찾는 중"),
    IN_PROGRESS("물건 전달 중"),
    COMPLETED("거래 완료");

    private final String description;
}
