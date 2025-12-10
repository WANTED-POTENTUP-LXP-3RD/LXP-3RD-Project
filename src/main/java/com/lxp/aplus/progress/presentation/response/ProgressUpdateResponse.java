package com.lxp.aplus.progress.presentation.response;

import java.time.LocalDateTime;

public record ProgressUpdateResponse(
        Long resourceId,
        Long enrollmentId,
        int progressRate,
        Long lastVideoId,
        Integer lastWatchedDuration,
        LocalDateTime updatedAt
) {
}
