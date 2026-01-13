package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.PresignedUrlResult;

public record PresignedUrlResponse(
        String presignedUrl,
        String key,
        String method,
        int expireSeconds
) {
    public static PresignedUrlResponse from(PresignedUrlResult result) {
        return new PresignedUrlResponse(
                result.url(),
                result.key(),
                result.method(),
                result.expireSeconds()
        );
    }
}
