package com.lxp.aplus.progress.presentation.response;

import java.time.LocalDateTime;

public record ProgressUpdateResponse(
    Long resourceId,
    Long enrollmentId,
    int progressRate,
    Long lastVideoId,
    int lastWatchedDuration,
    LocalDateTime updatedAt
) {
    public static ProgressUpdateResponse of(Long resourceId, Long enrollmentId, int progressRate, Long lastVideoId, int lastWatchedDuration, LocalDateTime updatedAt) {
        return new ProgressUpdateResponse(resourceId, enrollmentId, progressRate, lastVideoId, lastWatchedDuration, updatedAt);
    }
}
