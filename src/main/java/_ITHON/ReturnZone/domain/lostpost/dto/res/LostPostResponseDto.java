package _ITHON.ReturnZone.domain.lostpost.dto.res;

import _ITHON.ReturnZone.domain.lostpost.entity.LostPost;
import _ITHON.ReturnZone.domain.lostpost.entity.RegistrationType;
import _ITHON.ReturnZone.domain.lostpost.util.TimeUtil;
import _ITHON.ReturnZone.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Schema(description = "분실물 미리보기 응답 DTO")
public class LostPostResponseDto {

    @Schema(description = "분실물 ID", example = "1")
    private final Long lostPostId;

    @Schema(description = "이미지 URL", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/lostPosts/lostpost1.jpg")
    private final List<String> imageUrls;

    @Schema(description = "게시글 등록 유형", example = "LOST")
    private final RegistrationType registrationType;

    @Schema(description = "분실물 제목", example = "소니 헤드셋")
    private final String title;

    @Schema(description = "유저 프로필", example = "https://your-bucket.s3.ap-northeast-2.amazonaws.com/lostPosts/lostpost1.jpg")
    private final String memberImageUrl;

    @Schema(description = "유저 이름", example = "유저 1")
    private final String nickname;

    @Schema(description = "업로드된 지 몇 시간 전", example = "3시간 전")
    private final String timeAgo;

    @Schema(description = "분실 지역(동)", example = "월계 1동")
    private final String lostLocationDong; // 필드명 lostLocationDong으로 유지

    @Schema(description = "상세 위치", example = "주민센터 앞")
    private final String detailedLocation; // 필드명 detailedLocation으로 유지

    @Schema(description = "위도",   example = "37.5665")
    private final Double latitude;

    @Schema(description = "경도",   example = "126.9780")
    private final Double longitude;

    @Schema(description = "현상금 (원)", example = "10000")
    private final BigDecimal reward;

    @Schema(description = "즉시 정산 가능 여부", example = "true")
    private final boolean instantSettlement;

    @Schema(description = "본문 내용", example = "글 본문")
    private final String description; // 필드명 description으로 유지

    @Schema(description = "카테고리", example = "전자기기")
    private final String category;

    @Schema(description = "물품명", example = "아이폰 15 pro")
    private final String itemName;

    @Schema(description = "특징 1", example = "빨간색")
    private final String feature1;
    @Schema(description = "특징 2", example = "새 제품")
    private final String feature2;
    @Schema(description = "특징 3", example = "스크래치 없음")
    private final String feature3;
    @Schema(description = "특징 4", example = "박스 포함")
    private final String feature4;
    @Schema(description = "특징 5", example = "정품")
    private final String feature5;

    @Schema(description = "분실 시작 시간", example = "2025-06-25T12:00:00")
    private final LocalDateTime lostDateTimeStart;
    @Schema(description = "분실 종료 시간", example = "2025-06-25T13:00:00")
    private final LocalDateTime lostDateTimeEnd;

    @Schema(description = "비슷한 다른 글")
    private final List<SimpleLostPostResponseDto> similarLostPosts;

    @Builder
    private LostPostResponseDto(LostPost lostPost, Member member, List<SimpleLostPostResponseDto> similarLostPosts) {
        this.lostPostId = lostPost.getId();
        this.title = lostPost.getTitle();
        this.memberImageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.timeAgo = TimeUtil.getTimeAgo(lostPost.getCreatedAt());

        this.lostLocationDong = lostPost.getLostLocationDong(); // 엔티티의 lostLocationDong 매핑
        this.detailedLocation = lostPost.getDetailedLocation(); // 엔티티의 detailedLocation 매핑

        this.latitude = lostPost.getLatitude();
        this.longitude = lostPost.getLongitude();
        this.reward = lostPost.getReward();
        this.imageUrls = lostPost.getImageUrls();
        this.instantSettlement = lostPost.isInstantSettlement();

        this.description = lostPost.getDescription(); // 엔티티의 description 매핑

        this.category = lostPost.getCategory();
        this.itemName = lostPost.getItemName();

        this.feature1 = lostPost.getFeature1();
        this.feature2 = lostPost.getFeature2();
        this.feature3 = lostPost.getFeature3();
        this.feature4 = lostPost.getFeature4();
        this.feature5 = lostPost.getFeature5();

        this.lostDateTimeStart = lostPost.getLostDateTimeStart();
        this.lostDateTimeEnd = lostPost.getLostDateTimeEnd();
        this.registrationType = lostPost.getRegistrationType();

        this.similarLostPosts = similarLostPosts;
    }
}