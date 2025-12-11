package com.lxp.aplus.progress.application.result;

import com.lxp.aplus.enrollment.application.port.out.LectureResourceSummary;
import com.lxp.aplus.progress.domain.Progress;
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
    public static LectureProgressResult from(LectureResourceSummary resourceSummary, Progress progress) {
        int watched = progress != null ? progress.getWatchedDuration() : 0;
        boolean completed = progress != null && progress.isCompleted();
        LocalDateTime lastWatched = progress != null ? progress.getLastWatchedAt() : null;

        int totalDuration = resourceSummary.totalDurationSeconds() != null ? resourceSummary.totalDurationSeconds() : 0;

        int progressRate = 0;
        if (totalDuration > 0) {
            progressRate = (int) ((double) watched / totalDuration * 100);
        } else if (completed) {
            progressRate = 100;
        }

        return new LectureProgressResult(
                resourceSummary.resourceId(),
                resourceSummary.title(),
                progressRate,
                watched,
                totalDuration,
                completed,
                lastWatched
        );
    }

    public static LectureProgressResult from(LectureResourceSummary resourceSummary) {
        return from(resourceSummary, null);
    }
}