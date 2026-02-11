package com.lxp.aplus.course.application.port.in.dto;

import com.lxp.aplus.course.domain.ResourceType;

public record ResourceSummary(
        Long resourceId,
        String title,
        int totalDurationSeconds,
        ResourceType resourceType
) {
}
