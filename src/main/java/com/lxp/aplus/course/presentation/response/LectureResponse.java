package com.lxp.aplus.course.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.application.result.LectureResult;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LectureResponse(
        Long id,
        Long lectureId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LectureResourceResponse resource,
        List<ResourceResponse> resources

) {
    public static LectureResponse from(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .resources(lecture.getLectureResources().stream()
                        .map(ResourceResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
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
