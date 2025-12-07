package com.lxp.aplus.course.application.command;

import lombok.Builder;

@Builder
public record CreateLectureCommand(
        String title,
        String description
) {
}
