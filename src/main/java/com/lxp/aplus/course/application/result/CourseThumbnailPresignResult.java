package com.lxp.aplus.course.application.result;

public record CourseThumbnailPresignResult(
        String uploadUrl,     // S3 presigned PUT url
        String fileKey,       // courses/thumbnails/...
        String fileUrl,       // CDN or public access URL (미리보기용)
        int expiresInSeconds  // presigned URL 만료 시간(초)
) {
    public static CourseThumbnailPresignResult of(
            String uploadUrl,
            String fileKey,
            String fileUrl,
            int expiresInSeconds
    ) {
        return new CourseThumbnailPresignResult(
                uploadUrl,
                fileKey,
                fileUrl,
                expiresInSeconds
        );
    }
}
