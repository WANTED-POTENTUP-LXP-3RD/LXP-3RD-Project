package com.lxp.aplus.enrollment.presentation.response;

import com.lxp.aplus.enrollment.application.result.EnrollmentDetailResult;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import java.time.LocalDateTime;

public record EnrollmentDetailResponse(
        Long enrollmentId,
        Long studentId,
        Long courseId,
        EnrollmentStatus status,
        int overallProgressRate,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static EnrollmentDetailResponse from(EnrollmentDetailResult result) {
        return new EnrollmentDetailResponse(
                result.enrollmentId(),
                result.studentId(),
                result.courseId(),
                result.status(),
                result.overallProgressRate(),
                result.createdAt(),
                result.expiredAt()
        );
    }
}
