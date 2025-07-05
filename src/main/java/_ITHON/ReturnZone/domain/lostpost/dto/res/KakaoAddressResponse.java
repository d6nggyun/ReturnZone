package _ITHON.ReturnZone.domain.lostpost.dto.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KakaoAddressResponse {
    private List<Document> documents;

    @Getter
    @Setter
    @ToString
    public static class Document {
        private String address_name; // 전체 주소
        private Address address; // 지번 주소 정보
        private RoadAddress road_address; // 도로명 주소 정보
    }

    @Getter
    @Setter
    @ToString
    public static class Address {
        private String address_name;
        private String region_3depth_name; // 읍/면/동
        private String region_2depth_name; // 시/군/구
        // 추가로 필요한 필드가 있다면 여기에 추가
    }

    @Getter
    @Setter
    @ToString
    public static class RoadAddress {
        private String address_name;
        private String region_3depth_name; // 도로명 주소의 읍/면/동
        private String region_2depth_name; // 도로명 주소의 시/군/구
        // 추가로 필요한 필드가 있다면 여기에 추가
    }
}