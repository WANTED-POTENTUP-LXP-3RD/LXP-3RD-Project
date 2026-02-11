package com.lxp.aplus.progress.domain.vo;

import com.lxp.aplus.course.domain.ResourceType;

public record LectureSummary(
        Long resourceId,
        String title,
        ResourceType resourceType,
        int totalDurationSeconds
) {
}
