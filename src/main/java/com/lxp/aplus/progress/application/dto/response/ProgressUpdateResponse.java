package com.lxp.aplus.progress.application.dto.response;

import com.lxp.aplus.progress.domain.Progress;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProgressUpdateResponse {
    private final Long progressId;
    private final Long enrollmentId;
    private final Long resourceId;
    private final int watchedDuration;
    private final boolean completed;
    private final LocalDateTime updatedAt;

    private ProgressUpdateResponse(Long progressId, Long enrollmentId, Long resourceId, int watchedDuration, boolean completed, LocalDateTime updatedAt) {
        this.progressId = progressId;
        this.enrollmentId = enrollmentId;
        this.resourceId = resourceId;
        this.watchedDuration = watchedDuration;
        this.completed = completed;
        this.updatedAt = updatedAt;
    }

    public static ProgressUpdateResponse from(Progress progress) {
        return new ProgressUpdateResponse(
                progress.getId(),
                progress.getEnrollmentId(),
                progress.getLectureResourceId(),
                progress.getWatchedDuration(),
                progress.isCompleted(),
                progress.getLastWatchedAt()
        );
    }
}
