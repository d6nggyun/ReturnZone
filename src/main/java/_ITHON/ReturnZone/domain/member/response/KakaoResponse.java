package _ITHON.ReturnZone.domain.member.response; // <-- 이 부분이 수정되었어!

import lombok.Getter;

@Getter
public class KakaoResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    // 성공 응답 생성 메서드
    public static <T> KakaoResponse<T> onSuccess(T data) {
        return new KakaoResponse<>(true, "카카오 로그인 성공", data);
    }

    // 실패 응답 생성 메서드 (필요시 사용)
    public static <T> KakaoResponse<T> onFailure(String message) {
        return new KakaoResponse<>(false, message, null);
    }

    private KakaoResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}