package _ITHON.ReturnZone.domain.lostpost.dto.res;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.entity.RegistrationType;
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
    private final String location; // DTO 필드명은 'location'으로 유지 (표시용)

    @Schema(description = "현상금 (원)", example = "10000")
    private final BigDecimal reward;

    @Schema(description = "대표 이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/lostPosts/lostpost1.jpg")
    private final String mainImageUrl;

    @Schema(description = "즉시 정산 가능 여부", example = "true")
    private final boolean instantSettlement;

    @Schema(description = "게시글 등록 유형", example = "LOST")
    private final RegistrationType registrationType;

    @Schema(description = "분실물 상태 여부", example = "주인 찾는 중")
    private final String status;

    @Builder
    private SimpleLostPostResponseDto(LostPost lostPost) {
        this.lostPostId = lostPost.getId();
        this.title = lostPost.getTitle();
        this.timeAgo = TimeUtil.getTimeAgo(lostPost.getCreatedAt());
        this.location = lostPost.getLostLocationDong(); // <-- 엔티티의 lostLocationDong 매핑
        this.reward = lostPost.getReward();
        this.mainImageUrl = lostPost.getImageUrls().isEmpty()
                ? null
                : lostPost.getImageUrls().get(0);
        this.instantSettlement = lostPost.isInstantSettlement();
        this.registrationType = lostPost.getRegistrationType();
        this.status = lostPost.getStatus().getDescription();
    }
}