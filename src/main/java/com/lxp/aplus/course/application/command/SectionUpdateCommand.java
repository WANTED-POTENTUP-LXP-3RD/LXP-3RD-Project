package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record SectionUpdateCommand(
        Long courseId,
        Long instructorId,
        Long sectionId,
        String title,
        Integer orderIndex
) {
}
