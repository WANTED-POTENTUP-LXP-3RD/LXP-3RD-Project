package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.LectureResult;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LectureResponse (
        Long id,
        String title,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LectureResourceResponse resource
) {
    public static LectureResponse from (LectureResult result) {
        return LectureResponse.builder()
                .id(result.id())
                .title(result.title())
                .orderIndex(result.orderIndex())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .resource(new LectureResourceResponse(result.resource().resourceType(), result.resource().isDownloadable(), result.resource().fileUrl()))
                .build();
    }
}
