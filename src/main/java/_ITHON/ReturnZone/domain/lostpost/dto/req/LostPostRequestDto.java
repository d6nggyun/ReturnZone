package _ITHON.ReturnZone.domain.lostpost.dto.req;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class LostPostRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 15, message = "제목은 최대 15글자까지 입력 가능합니다.")
    private String title;

    private List<String> imageUrls;

    @NotBlank(message = "본문 내용은 필수입니다.") // description 필드에 맞게 수정
    private String description; // 필드명 content -> description으로 변경

    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;

    @NotBlank(message = "물품명은 필수입니다.")
    private String itemName;

    // 분실 지역(동)은 백엔드에서 카카오 API를 통해 자동으로 채워집니다.
    // 사용자가 직접 입력하지 않습니다.
    private String lostLocationDong;

    @NotBlank(message = "상세 위치는 필수입니다.") // detailedLocation 사용
    private String detailedLocation;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "분실 시간 시작은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lostDateTimeStart;

    @NotNull(message = "분실 시간 종료는 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lostDateTimeEnd;

    private String feature1;
    private String feature2;
    private String feature3;
    private String feature4;
    private String feature5;

    @NotNull(message = "현상금은 필수입니다.")
    @DecimalMin(value = "0.00", message = "현상금은 0원 이상이어야 합니다.")
    private BigDecimal reward;

    private boolean instantSettlement;
}