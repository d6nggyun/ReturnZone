package _ITHON.ReturnZone.global.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Schema(description = "페이지 / Slice 응답 공통 래퍼")
public class SliceResponse<T> {

    @ArraySchema(arraySchema = @Schema(description = "데이터 목록"))
    private final List<T> content;

    @Schema(description = "페이징 정보")
    private final PageInfo pageInfo;

    @Builder
    public SliceResponse(List<T> content, PageInfo pageInfo) {
        this.content  = content;
        this.pageInfo = pageInfo;
    }

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .pageInfo(new PageInfo(slice.getNumber(),
                        slice.getSize(),
                        slice.hasNext()))
                .build();
    }
}
