package com.lxp.aplus.course.domain;

import com.lxp.aplus.course.application.command.UpdateLectureCommand;
import lombok.Builder;
import org.hibernate.sql.Update;

@Builder
public record UpdateLectureSpec(
        Long lectureId,
        String title,
        Integer totalDurationSeconds,
        boolean isPreview,
        int orderIndex,
        UpdateLectureResourceSpec resource
) {
    public static UpdateLectureSpec from(Long lectureId, UpdateLectureCommand command) {
        return UpdateLectureSpec.builder()
                .lectureId(lectureId)
                .title(command.title())
                .totalDurationSeconds(command.totalDurationSeconds())
                .isPreview(command.isPreview())
                .orderIndex(command.orderIndex())
                .resource(new UpdateLectureResourceSpec(command.resource().isDownloadable()))
                .build();
    }

}
