package com.lxp.aplus.progress.presentation.response;

import com.lxp.aplus.progress.domain.Progress;
import java.time.LocalDateTime;

public record ProgressUpdateResponse(
        Long resourceId,
        Long enrollmentId,
        int progressRate,
        Long lastVideoId,
        Integer lastWatchedDuration,
        LocalDateTime updatedAt
) {
    public static ProgressUpdateResponse of(Progress progress, int currentProgressRate) {
        return new ProgressUpdateResponse(
                progress.getLectureResource().getId(),
                progress.getEnrollment().getId(),
                currentProgressRate,
                progress.getLectureResource().getId(),
                progress.getWatchedDuration(),
                progress.getLastWatchedAt()
        );
    }
}
