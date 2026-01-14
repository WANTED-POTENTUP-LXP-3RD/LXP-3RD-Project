package com.lxp.aplus.progress.presentation.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CourseProgressResponse {
    private final Long enrollmentId;
    private final int overallProgressRate;
    private final Long lastWatchedResourceId;
    private final LocalDateTime lastWatchedAt;
    private final List<LectureProgressResponse> lectureProgresses;

    public CourseProgressResponse(Long enrollmentId, int overallProgressRate, Long lastWatchedResourceId, LocalDateTime lastWatchedAt, List<LectureProgressResponse> lectureProgresses) {
        this.enrollmentId = enrollmentId;
        this.overallProgressRate = overallProgressRate;
        this.lastWatchedResourceId = lastWatchedResourceId;
        this.lastWatchedAt = lastWatchedAt;
        this.lectureProgresses = lectureProgresses;
    }
}
