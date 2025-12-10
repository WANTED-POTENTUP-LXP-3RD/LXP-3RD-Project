package com.lxp.aplus.course.domain;

import com.lxp.aplus.course.application.command.CreateLectureCommand;
import com.lxp.aplus.course.presentation.request.LectureResourceRequest;
import lombok.Builder;

@Builder
public record CreateLectureSpec(
        Long sectionId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        CreateLectureResourceSpec resource
) {
    public static CreateLectureSpec from(Long sectionId, CreateLectureCommand command) {
        return CreateLectureSpec.builder()
                .sectionId(sectionId)
                .title(command.title())
                .totalDurationSeconds(command.totalDurationSeconds())
                .isPreview(command.isPreview())
                .orderIndex(command.orderIndex())
                .resource(new CreateLectureResourceSpec(command.resource().isDownloadable()))
                .build();
    }
}
