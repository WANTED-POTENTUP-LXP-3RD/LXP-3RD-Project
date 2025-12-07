package com.lxp.aplus.course.application.result;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.Lecture;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LectureResult (
        Long id,
        String title,
        String description,
        int orderIndex,
        LocalDateTime createdAt

) {
    public static LectureResult from (Lecture lecture) {
        return LectureResult.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .orderIndex(lecture.getOrderIndex())
                .createdAt(lecture.getCreatedAt())
                .build();
    }
}
