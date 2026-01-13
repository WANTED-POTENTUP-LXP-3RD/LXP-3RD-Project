package com.lxp.aplus.progress.presentation.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LectureProgressResponse {
    private final Long resourceId;
    private final String title;
    private final int watchedDuration;
    private final int totalDurationSeconds;
    private final boolean completed;
    private final LocalDateTime lastWatchedAt;
    private final int progressRate;

    public LectureProgressResponse(Long resourceId, String title, int watchedDuration, int totalDurationSeconds, boolean completed, LocalDateTime lastWatchedAt) {
        this.resourceId = resourceId;
        this.title = title;
        this.watchedDuration = watchedDuration;
        this.totalDurationSeconds = totalDurationSeconds;
        this.completed = completed;
        this.lastWatchedAt = lastWatchedAt;
        this.progressRate = calculateProgressRate(watchedDuration, totalDurationSeconds, completed);
    }

    private int calculateProgressRate(int watched, int total, boolean isCompleted) {
        if (total == 0) {
            return isCompleted ? 100 : 0;
        }
        return (int) (((double) watched / total) * 100);
    }
}
