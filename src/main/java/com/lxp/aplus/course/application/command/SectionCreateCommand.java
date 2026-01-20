package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record SectionCreateCommand(
        Long courseId,
        Long instructorId,
        String title,
        Integer orderIndex
) {
}
