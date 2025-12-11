package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.presentation.request.LectureResourceRequest;
import lombok.Builder;

@Builder
public record UpdateLectureCommand(
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        LectureResourceRequest resource
) {
}
