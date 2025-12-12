package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record UpdateLectureCommand(
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        CreateLectureResourceCommand resource
) {
}
