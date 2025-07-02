package _ITHON.ReturnZone.domain.lostpost.dto.res;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.util.TimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Schema(description = "분실물 미리보기 응답 DTO")
public class LostPostResponseDto {

    @Schema(description = "분실물 ID", example = "1")
    private final Long lostPostId;

    @Schema(description = "분실물 제목", example = "소니 헤드셋")
    private final String title;

    @Schema(description = "유저 이름", example = "유저 1")
    private final String nickname;

    @Schema(description = "업로드된 지 몇 시간 전", example = "3시간 전")
    private final String timeAgo;

    @Schema(description = "위치", example = "월계 1동")
    private final String location;

    @Schema(description = "상세 위치", example = "주민센터 앞")
    private final String locationDetail;

    @Schema(description = "위도",   example = "37.5665")
    private final Double latitude;

    @Schema(description = "경도",   example = "126.9780")
    private final Double longitude;

    @Schema(description = "현상금 (원)", example = "10000")
    private final BigDecimal reward;

    @Schema(description = "이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/lostPosts/lostpost1.jpg")
    private final List<String> imageUrls;

    @Schema(description = "즉시 정산 가능 여부", example = "true")
    private final boolean instantSettlement;

    @Schema(description = "본문 내용", example = "글 본문")
    private final String description;

    @Schema(description = "카테고리", example = "전자기기")
    private final String category;

    @Schema(description = "물품명", example = "아이폰 15 pro")
    private final String itemName;

    @Builder
    private LostPostResponseDto(LostPost lostPost, String nickname) {
        this.lostPostId = lostPost.getId();
        this.title = lostPost.getTitle();
        this.nickname = nickname;
        this.timeAgo = TimeUtil.getTimeAgo(lostPost.getCreatedAt());
        this.location = lostPost.getLocation();
        this.locationDetail = lostPost.getLocationDetail();
        this.latitude = lostPost.getLatitude();
        this.longitude = lostPost.getLongitude();
        this.reward = lostPost.getReward();
        this.imageUrls = lostPost.getImageUrls();
        this.instantSettlement = lostPost.isInstantSettlement();
        this.description = lostPost.getDescription();
        this.category = lostPost.getCategory();
        this.itemName = lostPost.getItemName();
    }
}
