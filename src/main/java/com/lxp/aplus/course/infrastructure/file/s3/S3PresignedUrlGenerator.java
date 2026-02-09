package com.lxp.aplus.course.infrastructure.file.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.infrastructure.file.LectureResourceKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "s3", matchIfMissing = true)
public class S3PresignedUrlGenerator implements PresignedUrlGenerator {

    private final AmazonS3 amazonS3Client;
    private final LectureResourceKeyGenerator keyGenerator;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.expire_seconds}")
    private int expireMilliSeconds;

    @Override
    public PresignedUrlResult generateGetUrl(String key) {
        Date expiration = getExpiration();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getGetGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return new PresignedUrlResult(
                url.toExternalForm(),
                key,
                "GET",
                expireMilliSeconds / 1000
        );
    }

    @Override
    public PresignedUrlResult generatePutUrl(String originalFileName, String contentType) {
        Date expiration = getExpiration();
        String key = keyGenerator.generate(bucket, originalFileName);
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getPostGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return new PresignedUrlResult(
                url.toExternalForm(),
                key,
                "PUT",
                expireMilliSeconds / 1000
        );
    }

    @Override
    public PresignedUrlResult generateDeleteUrl(String key) {
        return null;
    }

    private GeneratePresignedUrlRequest getPostGeneratePresignedUrlRequest(String key, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
    }

    private GeneratePresignedUrlRequest getGetGeneratePresignedUrlRequest(String key, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
    }

    private Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += expireMilliSeconds; // 15분
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // 썸네일 PresignedURL 발급을 위한 메서드 재정의

    @Override
    public PresignedUrlResult generatePutUrlWithKey(String key, String contentType) {
        Date expiration = getExpiration();

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, key)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = amazonS3Client.generatePresignedUrl(req);

        return new PresignedUrlResult(
                url.toExternalForm(),
                key,
                "PUT",
                expireMilliSeconds / 1000
        );
    }

}
