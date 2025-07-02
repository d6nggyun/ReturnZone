package _ITHON.ReturnZone.domain.lostpost.dto.res;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Schema(description = "분실물 미리보기 응답 DTO")
public class SimpleLostPostResponseDto {

    @Schema(description = "분실물 ID", example = "1")
    private final Long lostPostId;

    @Schema(description = "분실물 제목", example = "소니 헤드셋")
    private final String title;

    @Schema(description = "업로드된 지 몇 시간 전", example = "3시간 전")
    private final String timeAgo;

    @Schema(description = "위치", example = "월계 1동")
    private final String location;

    @Schema(description = "현상금 (원)", example = "10000")
    private final BigDecimal reward;

    @Schema(description = "대표 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/lostPosts/lostpost1.jpg")
    private final String mainImageUrl;

    @Schema(description = "즉시 정산 가능 여부", example = "true")
    private final boolean instantSettlement;

    @Builder
    private SimpleLostPostResponseDto(LostPost lostPost) {
        this.lostPostId = lostPost.getId();
        this.title = lostPost.getTitle();
        this.timeAgo = TimeUtil.getTimeAgo(lostPost.getCreatedAt());
        this.location = lostPost.getLocation();
        this.reward = lostPost.getReward();
        this.mainImageUrl = lostPost.getImageUrls().isEmpty()
                ? null
                : lostPost.getImageUrls().get(0);
        this.instantSettlement = lostPost.isInstantSettlement();
    }
}
