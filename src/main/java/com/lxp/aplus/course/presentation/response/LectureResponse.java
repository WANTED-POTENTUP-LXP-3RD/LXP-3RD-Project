package com.lxp.aplus.course.presentation.response;

import com.lxp.aplus.course.application.result.LectureResult;
import com.lxp.aplus.course.domain.Lecture;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LectureResponse (
        Long id,
        String title,
        String description,
        int orderIndex,
        LocalDateTime createdAt

) {
    public static LectureResponse from (LectureResult result) {
        return LectureResponse.builder()
                .id(result.id())
                .title(result.title())
                .description(result.description())
                .orderIndex(result.orderIndex())
                .createdAt(result.createdAt())
                .build();
    }
}
