package com.lxp.aplus.progress.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lxp.aplus.progress.application.result.LectureProgressResult;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LectureProgressResponse(
        Long resourceId,
        String title,
        int currentProgressRate,
        int watchedDuration,
        int totalDurationSeconds,
        boolean isCompleted,
        LocalDateTime lastWatchedAt
) {
    public static LectureProgressResponse from(LectureProgressResult result) {
        return new LectureProgressResponse(
                result.resourceId(),
                result.title(),
                result.currentProgressRate(),
                result.watchedDuration(),
                result.totalDurationSeconds(),
                result.isCompleted(),
                result.lastWatchedAt()
        );
    }
}
