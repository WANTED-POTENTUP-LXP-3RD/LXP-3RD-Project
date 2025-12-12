package com.lxp.aplus.enrollment.application.port.out;

public record LectureResourceSummary(
        Long resourceId,
        String title,
        Integer totalDurationSeconds
) {
}
