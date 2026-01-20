package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import com.lxp.aplus.course.domain.CourseLevel;

public record CourseUpdateRequest(
        String title,
        String summary,
        String description,
        Long categoryId,
        CourseLevel courseLevel,
        String thumbnailUrl,
        Integer price
) {
    public CourseUpdateCommand toCommand(Long courseId, Long instructorId) {
        return CourseUpdateCommand.builder()
                .instructorId(instructorId)
                .courseId(courseId)
                .title(this.title)
                .summary(this.summary)
                .description(this.description)
                .categoryId(this.categoryId)
                .courseLevel(this.courseLevel)
                .thumbnailUrl(this.thumbnailUrl)
                .price(this.price)
                .build();
    }
}
