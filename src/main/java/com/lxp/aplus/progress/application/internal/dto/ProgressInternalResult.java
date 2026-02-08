package com.lxp.aplus.progress.application.internal.dto;

import com.lxp.aplus.progress.domain.Progress;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProgressInternalResult(
        Long progressId,
        Long enrollmentId,
        Long lectureResourceId,
        int watchedDuration,
        boolean completed,
        LocalDateTime lastWatchedAt
) {
    public static ProgressInternalResult from(Progress progress) {
        return ProgressInternalResult.builder()
                .progressId(progress.getId())
                .enrollmentId(progress.getEnrollmentId())
                .lectureResourceId(progress.getLectureResourceId())
                .watchedDuration(progress.getWatchedDuration())
                .completed(progress.isCompleted())
                .lastWatchedAt(progress.getLastWatchedAt())
                .build();
    }
}
