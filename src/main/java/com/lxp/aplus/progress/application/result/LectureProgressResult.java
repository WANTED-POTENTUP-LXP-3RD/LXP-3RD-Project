package com.lxp.aplus.progress.application.result;

import java.time.LocalDateTime;

public record LectureProgressResult(
        Long resourceId,
        String title,
        int currentProgressRate,
        int watchedDuration,
        int totalDuration,
        boolean isCompleted,
        LocalDateTime lastWatchedAt
) {
    public static LectureProgressResult of(Long resourceId, String title, int currentProgressRate, int watchedDuration, int totalDuration, boolean isCompleted, LocalDateTime lastWatchedAt) {
        return new LectureProgressResult(resourceId, title, currentProgressRate, watchedDuration, totalDuration, isCompleted, lastWatchedAt);
    }
}
