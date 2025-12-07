package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.CreateLectureCommand;

public record LectureCreateRequest(
        String title,
        String description
) {
    public CreateLectureCommand toCommand() {
        return CreateLectureCommand.builder()
                .title(this.title)
                .description(this.description)
                .build();
    }
}


