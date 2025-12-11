package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record SectionUpdateCommand(
        String title,
        Integer orderIndex
) {
}
