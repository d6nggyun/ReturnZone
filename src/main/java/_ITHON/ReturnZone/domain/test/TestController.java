package _ITHON.ReturnZone.domain.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
@Tag(name = "Test", description = "S3 업로드 및 DB 저장 테스트 API")
public class TestController {

    private final TestService testService;

    @Operation(summary = "테스트 파일 업로드 및 DB 저장", description = "이미지 파일과 텍스트를 받아 S3에 업로드하고, 그 정보를 DB에 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "파일 업로드 및 DB 저장 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TestFileEntity.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 업로드 실패", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업로드할 이미지 파일과 이미지 설명 텍스트",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = TestFileUploadRequest.class) // <--- 이 부분만 수정!
                    )
            )
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TestFileEntity> uploadFile(
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile,
            @Parameter(description = "이미지에 대한 설명 텍스트", example = "테스트 이미지입니다.")
            @RequestPart("imgText") String imgText) {

        TestFileEntity savedEntity = testService.saveTestFile(imgFile, imgText);
        return ResponseEntity.ok(savedEntity);
    }
}