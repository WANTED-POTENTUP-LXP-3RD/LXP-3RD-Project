package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.domain.LectureResource;
import lombok.Builder;

import java.time.LocalDateTime;

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
        LectureResource lectureResource = lecture.getLectureResources().get(0);

        return LectureResult.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .totalDurationSeconds(lecture.getTotalDurationSeconds())
                .isPreview(lecture.isPreview())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .resource(new LectureResourceResult(lecture.getLectureResources().get(0).getResourceType(),
                        lecture.getLectureResources().get(0).isDownloadable(),
                        lecture.getLectureResources().get(0).getFileUrl()
                ))
                .resource(
                        LectureResourceResult.builder()
                                .resourceType(lectureResource.getResourceType())
                                .isDownloadable(lectureResource.isDownloadable())
                                .fileUrl(lectureResource.getFileUrl())
                                .build()
                )
                .build();
    }
}
