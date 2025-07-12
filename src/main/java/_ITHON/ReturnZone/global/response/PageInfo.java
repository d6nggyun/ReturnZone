package _ITHON.ReturnZone.global.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "페이징 정보")
public record PageInfo (
        // 현재 페이지(0-based)
        int page,
        // 한번에 가져온 건수
        int size,
        // 다음 페이지 존재 여부
        boolean hasNext
){ }
