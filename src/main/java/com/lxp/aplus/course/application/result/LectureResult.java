package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Lecture;
import com.lxp.aplus.course.presentation.response.LectureResourceResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LectureResult (
        Long id,
        String title,
        int orderIndex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LectureResourceResponse response
) {
    public static LectureResult from (Lecture lecture) {
        return LectureResult.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt())
                .response(new LectureResourceResponse(lecture.getLectureResources().get(0).getResourceType(), lecture.getLectureResources().get(0).isDownloadable(), lecture.getLectureResources().get(0).getFileUrl()
                ))
                .build();
    }
}
