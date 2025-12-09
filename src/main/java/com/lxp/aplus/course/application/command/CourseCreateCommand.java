package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.domain.CourseLevel;
import lombok.Builder;

@Builder
public record CourseCreateCommand(
        String title,
        Long categoryId,
        String summary,
        String description,
        String thumbnailUrl,
        int price,
        CourseLevel courseLevel
) {
}
