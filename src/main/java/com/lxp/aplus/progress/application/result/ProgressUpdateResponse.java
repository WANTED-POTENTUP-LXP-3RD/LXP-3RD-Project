package com.lxp.aplus.progress.application.result;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProgressUpdateResponse(
    Long resourceId,
    Long enrollmentId,
    int progressRate,
    Long lastVideoId,
    int lastWatchedDuration,
    LocalDateTime updatedAt
) {

}
