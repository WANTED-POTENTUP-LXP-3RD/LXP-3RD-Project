package com.lxp.aplus.progress.application.port;

import com.lxp.aplus.course.application.port.in.dto.ResourceSummary;
import com.lxp.aplus.course.domain.ResourceType;

public record LectureSummaryDto(
    Long resourceId,
    String title,
    int totalDurationSeconds,
    ResourceType resourceType
) {
    public static LectureSummaryDto from(ResourceSummary resource) {
        return new LectureSummaryDto(
                resource.resourceId(),
                resource.title(),
                resource.totalDurationSeconds(),
                resource.resourceType()
        );
    }
}
