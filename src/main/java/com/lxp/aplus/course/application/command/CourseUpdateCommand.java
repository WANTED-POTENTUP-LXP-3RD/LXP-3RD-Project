package com.lxp.aplus.course.application.command;

import com.lxp.aplus.course.domain.CourseLevel;
import lombok.Builder;

@Builder
public record CourseUpdateCommand(
        String title,
        String summary,
        String description,
        Long categoryId,
        CourseLevel courseLevel,
        String thumbnailUrl,
        Integer price
) {}
