package com.lxp.aplus.course.presentation.request;

import com.lxp.aplus.course.application.command.UpdateLectureCommand;

public record LectureUpdateRequest(
        String title,
        Integer totalDurationSeconds,
        Boolean isPreview,
        LectureResourceRequest resource
) {
    public UpdateLectureCommand toCommand() {
        return UpdateLectureCommand.builder()
                .title(this.title)
                .totalDurationSeconds(this.totalDurationSeconds)
                .isPreview(this.isPreview)
                .resource(this.resource)
                .build();
    }
}
