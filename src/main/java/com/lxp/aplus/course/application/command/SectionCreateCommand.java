package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record SectionCreateCommand(
        String title,
        Integer orderIndex
) {
}
