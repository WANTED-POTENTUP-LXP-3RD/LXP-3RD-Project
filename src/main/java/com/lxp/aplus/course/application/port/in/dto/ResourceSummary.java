package com.lxp.aplus.course.application.port.in.dto;

public record ResourceSummary(
        Long resourceId,
        String title,
        int totalDurationSeconds
) {
}
