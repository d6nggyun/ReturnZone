package _ITHON.ReturnZone.domain.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// 이 DTO는 실제 컨트롤러에서 @RequestBody로 받지 않고,
// 오직 스웨거 문서화를 위해 사용됩니다.
@Getter
@Setter
@Schema(name = "TestFileUploadRequest", description = "테스트 파일 업로드 요청 스키마")
public class TestFileUploadRequest {

    @Schema(type = "string", format = "binary", description = "업로드할 이미지 파일")
    private Object imgFile; // MultipartFile 대신 Object 또는 String(binary)으로 정의

    @Schema(type = "string", description = "이미지 설명 텍스트", example = "테스트 이미지입니다.")
    private String imgText;
}