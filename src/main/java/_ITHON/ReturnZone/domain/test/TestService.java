package _ITHON.ReturnZone.domain.test; // 패키지 변경

import _ITHON.ReturnZone.global.aws.s3.AwsS3Uploader; // AwsS3Uploader는 다른 패키지에 있으므로 import 필요
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestFileRepository testFileRepository; // 같은 패키지이므로 import 필요 없음
    private final AwsS3Uploader awsS3Uploader;

    @Transactional
    public TestFileEntity saveTestFile(MultipartFile imgFile, String imgText) {
        log.info("[TestService] saveTestFile 호출: imgText={}", imgText);

        String imgUrl = null;
        if (imgFile != null && !imgFile.isEmpty()) {
            try {
                imgUrl = awsS3Uploader.upload(imgFile, "test-images");
                log.info("S3 업로드 성공: URL={}", imgUrl);
            } catch (IOException e) {
                log.error("S3 이미지 업로드 실패: {}", e.getMessage(), e);
                throw new RuntimeException("테스트 이미지 업로드에 실패했습니다.", e);
            }
        } else {
            log.warn("업로드할 이미지 파일이 제공되지 않았습니다.");
        }

        TestFileEntity testFileEntity = new TestFileEntity(imgText, imgUrl); // 같은 패키지이므로 import 필요 없음
        TestFileEntity savedEntity = testFileRepository.save(testFileEntity);
        log.info("TestFileEntity 저장 성공: id={}, imgText={}, imgUrl={}",
                savedEntity.getId(), savedEntity.getImgText(), savedEntity.getImgUrl());

        return savedEntity;
    }
}