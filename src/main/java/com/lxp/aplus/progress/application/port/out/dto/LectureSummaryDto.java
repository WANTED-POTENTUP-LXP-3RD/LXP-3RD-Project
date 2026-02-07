package com.lxp.aplus.progress.application.port.out.dto;

import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.course.domain.ResourceType;

public record LectureSummaryDto(
        Long resourceId,
        String title,
        int totalDurationSeconds,
        ResourceType resourceType
) {
    public static LectureSummaryDto from(ResourceSummary summary) {
        return new LectureSummaryDto(
                summary.resourceId(),
                summary.title(),
                summary.totalDurationSeconds(),
                summary.resourceType()
        );
    }
}
