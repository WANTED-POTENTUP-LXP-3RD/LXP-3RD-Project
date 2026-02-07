package com.lxp.aplus.progress.application.port.out.dto;

public record EnrollmentStatusDto(
        Long enrollmentId,
        boolean isExpired
) {
}
