package com.lxp.aplus.progress.application.result;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record EnrollmentProgressResult(
        Long enrollmentId,
        int overallProgressRate,
        Long lastWatchedVideoId,
        int lastWatchedDurationOfLastVideo,
        LocalDateTime lastWatchedAt,
        List<LectureProgressResult> lectureProgresses
) {

}
