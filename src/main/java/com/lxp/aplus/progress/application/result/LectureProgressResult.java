package com.lxp.aplus.progress.application.result;

import com.lxp.aplus.enrollment.application.port.out.LectureResourceSummary;
import java.time.LocalDateTime;

public record LectureProgressResult(
        Long resourceId,
        String title,
        int currentProgressRate,
        int watchedDuration,
        int totalDurationSeconds,
        boolean isCompleted,
        LocalDateTime lastWatchedAt
) {
    public static LectureProgressResult of(
            LectureResourceSummary resourceSummary,
            int watchedDuration,
            boolean isCompleted,
            LocalDateTime lastWatchedAt,
            int currentProgressRate
    ) {
        int totalDuration = resourceSummary.totalDurationSeconds() != null ? resourceSummary.totalDurationSeconds() : 0;

        return new LectureProgressResult(
                resourceSummary.resourceId(),
                resourceSummary.title(),
                currentProgressRate,
                watchedDuration,
                totalDuration,
                isCompleted,
                lastWatchedAt
        );
    }

    public static LectureProgressResult from(LectureResourceSummary resourceSummary) {
        return of(resourceSummary, 0, false, null, 0); 
    }
}