package com.lxp.aplus.course.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Lecture;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LectureResponse(
        Long lectureId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LectureResourceResponse resource
) {
    public static LectureResponse from(Lecture lecture) {
        LectureResourceResponse resource = null;

        if (lecture.getLectureResources() != null && !lecture.getLectureResources().isEmpty()) {
            resource = LectureResourceResponse.from(lecture.getLectureResources().get(0));
        }

        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .resource(resource)
                .build();
    }
    public static LectureResponse from(LectureResult result) {
        return LectureResponse.builder()
                .lectureId(result.id())
                .title(result.title())
                .totalDurationSeconds(result.totalDurationSeconds())
                .isPreview(result.isPreview())
                .orderIndex(result.orderIndex())
                .createdAt(result.createdAt())
                .updatedAt(result.updatedAt())
                .resource(
                        LectureResourceResponse.builder()
                                .resourceType(result.resource().resourceType())
                                .isDownloadable(result.resource().isDownloadable())
                                .fileUrl(result.resource().fileUrl())
                                .build()
                )
                .build();
    }
}
