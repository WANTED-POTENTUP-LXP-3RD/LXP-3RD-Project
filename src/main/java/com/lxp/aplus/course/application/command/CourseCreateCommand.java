package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.domain.CourseLevel;
import lombok.Builder;

@Builder
public record CourseCreateCommand(
        String title,
        String summary,
        String description,
        Long categoryId,
        int price,
        String thumbnailUrl,
        CourseLevel courseLevel
) {
}
