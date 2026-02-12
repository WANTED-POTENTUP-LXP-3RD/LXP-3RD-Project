package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CourseUpdateCommand;
import com.lxp.aplus.course.domain.CourseLevel;

public record CourseUpdateRequest(
        String title,
        String summary,
        String description,
        Long categoryId,
        CourseLevel courseLevel,
        String thumbnailResourceKey,
        Integer price
) {
    public CourseUpdateCommand toCommand() {
        return CourseUpdateCommand.builder()
                .title(this.title)
                .summary(this.summary)
                .description(this.description)
                .categoryId(this.categoryId)
                .courseLevel(this.courseLevel)
                .thumbnailResourceKey(this.thumbnailResourceKey)
                .price(this.price)
                .build();
    }
}
