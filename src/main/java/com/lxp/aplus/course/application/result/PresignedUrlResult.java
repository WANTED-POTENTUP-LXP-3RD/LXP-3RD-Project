package com.lxp.aplus.course.application.result;

public record PresignedUrlResult(
        String url,
        String key,
        String method,
        int expireSeconds
) {
}
