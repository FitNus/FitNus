package com.sparta.fitnus.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String AWS_BUCKET;
    @Value("${cloud.aws.region.static}")
    private String AWS_REGION;

    public String uploadFile(MultipartFile file) throws IOException {
        // 파일 검증(크기와 형식)
        validateFile(file);

        // 고유한 파일 이름 생성
        String fileName = generateFileName(file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(AWS_BUCKET, fileName,
                file.getInputStream(), metadata);

        amazonS3.putObject(putObjectRequest);

        return getPublicUrl(fileName);
    }

    public void deleteFile(String fileUrl) {
        // S3에서 파일 삭제
        String splitString = ".com/";
        String fileName = fileUrl.substring(
                fileUrl.lastIndexOf(splitString) + splitString.length());
        amazonS3.deleteObject(new DeleteObjectRequest(AWS_BUCKET, fileName));
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
}
