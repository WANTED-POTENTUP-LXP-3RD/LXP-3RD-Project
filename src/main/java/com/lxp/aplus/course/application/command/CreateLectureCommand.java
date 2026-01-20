package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record CreateLectureCommand(
        Long courseId,
        Long sectionId,
        Long instructorId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        String resourceKey
) {
}
