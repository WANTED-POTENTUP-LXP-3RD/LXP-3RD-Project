package com.lxp.aplus.progress.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record LearningHistoryResult(
    Long enrollmentId,
    int overallProgressRate,
    Long lastWatchedVideoId,
    Integer lastWatchedDurationOfLastVideo,
    LocalDateTime lastWatchedAt,
    List<LectureProgressResult> lectureProgresses
) {
}
