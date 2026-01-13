package com.lxp.aplus.progress.application.port;

public record EnrollmentStatusDto(
        Long enrollmentId,
        boolean isExpired
) {
}
