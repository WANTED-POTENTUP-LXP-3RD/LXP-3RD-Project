package com.lxp.aplus.enrollment.application.internal.dto;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EnrollmentInternalResult(
        Long enrollmentId,
        Long studentId,
        Long courseId,
        boolean isCompleted,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static EnrollmentInternalResult from(Enrollment enrollment) {
        return EnrollmentInternalResult.builder()
                .enrollmentId(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .isCompleted(enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .createdAt(enrollment.getCreatedAt())
                .expiredAt(enrollment.getExpiredAt())
                .build();
    }
}
