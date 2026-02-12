package com.lxp.aplus.progress.application.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CourseProgressResponse {
    private final Long enrollmentId;
    private final int overallProgressRate;
    private final Long lastWatchedResourceId;
    private final LocalDateTime lastWatchedAt;
    private final List<ResourceProgressResponse> resourceProgresses;

    public CourseProgressResponse(Long enrollmentId, int overallProgressRate, Long lastWatchedResourceId, LocalDateTime lastWatchedAt, List<ResourceProgressResponse> resourceProgresses) {
        this.enrollmentId = enrollmentId;
        this.overallProgressRate = overallProgressRate;
        this.lastWatchedResourceId = lastWatchedResourceId;
        this.lastWatchedAt = lastWatchedAt;
        this.resourceProgresses = resourceProgresses;
    }
}
