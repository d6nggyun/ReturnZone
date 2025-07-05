package _ITHON.ReturnZone.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class AwsS3Uploader {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}") // application.yml
    private String bucket;

    /**
     * MultipartFile을 S3에 업로드하고 URL을 반환합니다.
     * @param multipartFile 업로드할 파일
     * @param dirName S3 버킷 내의 디렉토리 이름 (예: "images", "lost-posts")
     * @return 업로드된 파일의 S3 URL
     * @throws IOException 파일 변환 중 발생할 수 있는 예외
     */
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        // MultipartFile을 File로 변환 (로컬에 임시 파일 생성)
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));

        String uploadImageUrl;
        try {
            // S3에 업로드할 파일 이름 생성 (경로/고유한이름_원본이름)
            String fileName = dirName + "/" + generateFileName(uploadFile.getName());
            uploadImageUrl = putS3(uploadFile, fileName); // S3에 업로드
        } finally {
            removeNewFile(uploadFile); // 로컬에 생성된 임시 파일 삭제
        }
        return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    // 실질적인 S3 업로드 부분
    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
        );
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    // 로컬에 생성된 임시 파일 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("로컬 임시 파일이 삭제되었습니다: {}", targetFile.getName());
        } else {
            log.warn("로컬 임시 파일 삭제에 실패했습니다: {}", targetFile.getName());
        }
    }

    // MultipartFile을 File로 변환 (로컬에 파일 생성)
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) { // 파일 생성 시도
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty(); // 파일 생성 실패 시
    }

    // 파일 이름 중복 방지를 위해 UUID를 포함하여 파일 이름 생성
    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }
}