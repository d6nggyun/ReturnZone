package _ITHON.ReturnZone.domain.lostpost.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "lost_post")
public class LostPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 15)
    private String title;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "lost_post_images", joinColumns = @JoinColumn(name = "lost_post_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    // description 필드 사용 (이전 논의 반영)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String itemName;

    // 분실 지역 (동) - lostLocationDong 사용
    @Column(name = "lost_location_dong", nullable = false)
    private String lostLocationDong;

    // 상세 위치 - detailedLocation 사용
    @Column(name = "location_detail", nullable = false) // nullable = false 확인
    private String detailedLocation;

    @Column(nullable = false, precision = 10)
    private Double longitude;

    @Column(nullable = false, precision = 10)
    private Double latitude;

    @Column(name = "lost_date_time_start", nullable = false)
    private LocalDateTime lostDateTimeStart;

    @Column(name = "lost_date_time_end", nullable = false)
    private LocalDateTime lostDateTimeEnd;

    private String feature1;
    private String feature2;
    private String feature3;
    private String feature4;
    private String feature5;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal reward;

    @Column(name = "instant_settlement", nullable = false)
    private boolean instantSettlement = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 생성자 파라미터도 description으로 변경되었는지 확인
    public LostPost(Long memberId, String title, List<String> imageUrls, String description, String category,
                    String itemName, String lostLocationDong, String detailedLocation,
                    Double longitude, Double latitude, LocalDateTime lostDateTimeStart,
                    LocalDateTime lostDateTimeEnd, String feature1, String feature2,
                    String feature3, String feature4, String feature5, BigDecimal reward,
                    boolean instantSettlement) {
        this.memberId = memberId;
        this.title = title;
        if (imageUrls != null) {
            this.imageUrls = new ArrayList<>(imageUrls);
        }
        this.description = description; // 필드명과 파라미터명 일치
        this.category = category;
        this.itemName = itemName;
        this.lostLocationDong = lostLocationDong;
        this.detailedLocation = detailedLocation;
        this.longitude = longitude;
        this.latitude = latitude;
        this.lostDateTimeStart = lostDateTimeStart;
        this.lostDateTimeEnd = lostDateTimeEnd;
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.feature3 = feature3;
        this.feature4 = feature4;
        this.feature5 = feature5;
        this.reward = reward;
        this.instantSettlement = instantSettlement;
    }
}