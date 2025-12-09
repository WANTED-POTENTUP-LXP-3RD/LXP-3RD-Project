package com.lxp.aplus.enrollment.application.result;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentDetailResult(
        Long enrollmentId,
        Long studentId,
        Long courseId,
        EnrollmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static EnrollmentDetailResult from(Enrollment enrollment) {
        return new EnrollmentDetailResult(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getStatus(),
                enrollment.getCreatedAt(),
                enrollment.getExpiredAt()
        );
    }
}
