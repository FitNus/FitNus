package com.sparta.user.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String AWS_BUCKET;
    @Value("${cloud.aws.region.static}")
    private String AWS_REGION;

    public String uploadFile(MultipartFile file) throws IOException {
        // 파일 검증(크기와 형식)
        validateFile(file);

        // 고유한 파일 이름 생성
        String fileName = generateFileName(file);

        try {
            // S3에 파일 업로드
            s3Client.putObject(
                    software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                            .bucket(AWS_BUCKET)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (S3Exception e) {
            throw new IllegalArgumentException("S3에 파일 업로드를 실패했습니다.");
        }

        return getPublicUrl(fileName);
    }

    public void deleteFile(String fileUrl) {
        // 파일 URL에서 S3 키 이름 추출
        String key = extractKeyFromUrl(fileUrl);

        // S3에서 파일 삭제
        software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteObjectRequest =
                software.amazon.awssdk.services.s3.model.DeleteObjectRequest.builder()
                        .bucket(AWS_BUCKET)
                        .key(key)
                        .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // 파일 검증 로직
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 파일 크기 제한 (5MB 이하)
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB 제한
            throw new IllegalArgumentException("파일 크기는 최대 5MB입니다.");
        }

        // 지원되는 MIME 타입 목록
        String contentType = file.getContentType();
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
        }
    }

    // 지원되는 파일 형식 확인
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("application/pdf")
                || contentType.equals("text/csv");
    }

    // 고유한 파일 이름 생성
    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + "_" + file.getOriginalFilename();
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", AWS_BUCKET, AWS_REGION, fileName);
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
    }
}
