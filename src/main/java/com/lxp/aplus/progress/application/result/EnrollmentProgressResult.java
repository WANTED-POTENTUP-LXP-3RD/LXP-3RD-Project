package com.lxp.aplus.progress.application.result;

import java.time.LocalDateTime;
import java.util.List;

public record EnrollmentProgressResult(
        Long enrollmentId,
        int overallProgressRate,
        Long lastWatchedVideoId,
        int lastWatchedDurationOfLastVideo,
        LocalDateTime lastWatchedAt,
        List<LectureProgressResult> lectureProgresses
) {
    public static EnrollmentProgressResult of(Long enrollmentId, int overallProgressRate, Long lastWatchedVideoId, int lastWatchedDurationOfLastVideo, LocalDateTime lastWatchedAt, List<LectureProgressResult> lectureProgresses) {
        return new EnrollmentProgressResult(enrollmentId, overallProgressRate, lastWatchedVideoId, lastWatchedDurationOfLastVideo, lastWatchedAt, lectureProgresses);
    }
}
