package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.SectionUpdateCommand;

public record SectionUpdateRequest(
        String title
) {
    public SectionUpdateCommand toCommand() {
        return SectionUpdateCommand.builder()
                .title(this.title)
                .build();
    }
}
