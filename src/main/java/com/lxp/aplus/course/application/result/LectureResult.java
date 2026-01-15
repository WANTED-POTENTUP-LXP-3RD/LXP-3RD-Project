package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
public record LectureResult (
        Long id,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LectureResourceResult resource
) {
    public static LectureResult from(Lecture lecture) {
        LectureResourceResult resourceResult = Optional.ofNullable(lecture.getLectureResources())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(res -> LectureResourceResult.builder()
                        .resourceType(res.getResourceType())
                        .isDownloadable(res.isDownloadable())
                        .fileKey(res.getFileKey())
                        .build())
                .orElse(null);

        return LectureResult.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .resource(resourceResult)
                .build();
    }
}
