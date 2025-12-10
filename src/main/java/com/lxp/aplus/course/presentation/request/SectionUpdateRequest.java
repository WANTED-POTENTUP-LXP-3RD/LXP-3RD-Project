package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.SectionUpdateCommand;

public record SectionUpdateRequest(
        String title,
        Integer orderIndex
) {
    public SectionUpdateCommand toCommand() {
        return SectionUpdateCommand.builder()
                .title(this.title)
                .orderIndex(this.orderIndex)
                .build();
    }
}
