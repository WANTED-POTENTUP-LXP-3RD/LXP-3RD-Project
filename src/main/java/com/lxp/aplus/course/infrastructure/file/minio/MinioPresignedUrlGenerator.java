package com.lxp.aplus.course.infrastructure.file.minio;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import com.lxp.aplus.course.infrastructure.file.LectureResourceKeyGenerator;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.type", havingValue = "minio", matchIfMissing = true)
public class MinioPresignedUrlGenerator implements PresignedUrlGenerator {
    private final MinioClient minioClient;
    private final LectureResourceKeyGenerator keyGenerator;

    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.resource_expire_seconds}")
    private int expireSeconds;

    @Override
    public PresignedUrlResult generateGetUrl(String key) {
        Method method = Method.GET;
        String presignedUrl = generatePresignedUrl(key, method, Collections.emptyMap());

        return new PresignedUrlResult(
                presignedUrl,
                key,
                method.toString(),
                expireSeconds
        );
    }

    @Override
    public PresignedUrlResult generatePutUrl(String originalFileName, String contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        String key = keyGenerator.generate(bucket, originalFileName);

        Method method = Method.PUT;
        String presignedUrl = generatePresignedUrl(key, method, headers);

        return new PresignedUrlResult(
                presignedUrl,
                key,
                method.toString(),
                expireSeconds
        );
    }

    @Override
    public PresignedUrlResult generateDeleteUrl(String key) {
        Method method = Method.DELETE;
        String presignedUrl = generatePresignedUrl(key, method, Map.of());

        return new PresignedUrlResult(
                presignedUrl,
                key,
                method.toString(),
                expireSeconds
        );
    }

    private String generatePresignedUrl(String key, Method method, Map<String, String> headers) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .object(key)
                            .method(method)
                            .bucket(bucket)
                            .expiry(expireSeconds, TimeUnit.SECONDS)
                            .extraHeaders(headers)
                            .build()
            );
        } catch (InvalidKeyException e) {
            throw new BusinessException(LectureResourceErrorCode.LECTURE_RESOURCE_NOT_EXIST);
        } catch (Exception e) {
            throw new BusinessException(LectureResourceErrorCode.STORAGE_CLIENT_INTERNAL_ERROR);
        }
    }
}
