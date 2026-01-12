package com.lxp.aplus.course.infrastructure.file.minio;

import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.LectureResourceErrorCode;
import com.lxp.aplus.course.application.port.out.PresignedUrlGenerator;
import com.lxp.aplus.course.application.result.PresignedUrlResult;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioPresignedUrlGenerator implements PresignedUrlGenerator {
    private final MinioClient minioClient;
    private final LectureResourceKeyGenerator keyGenerator;

    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.resource_expire_seconds}")
    private int expireSeconds;

    @Override
    public PresignedUrlResult generateGetUrl(String key) {

        String presignedUrl = generatePresignedUrl(key, Method.GET, Map.of());

        return new PresignedUrlResult(
                presignedUrl,
                key,
                HttpMethod.GET.toString(),
                expireSeconds
        );
    }

    @Override
    public PresignedUrlResult generatePutUrl(String originalFileName, String contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);

        String key = keyGenerator.generate(bucket, originalFileName);

        String presignedUrl = generatePresignedUrl(key, Method.PUT, headers);

        return new PresignedUrlResult(
                presignedUrl,
                key,
                HttpMethod.PUT.toString(),
                expireSeconds
        );
    }

    @Override
    public PresignedUrlResult generateDeleteUrl(String key) {
        String presignedUrl = generatePresignedUrl(key, Method.DELETE, Map.of());

        return new PresignedUrlResult(
                presignedUrl,
                key,
                HttpMethod.GET.toString(),
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
