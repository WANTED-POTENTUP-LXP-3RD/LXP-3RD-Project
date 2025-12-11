package com.lxp.aplus.enrollment.application.result;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentListItemResult(
        Long enrollmentId,
        Long courseId,
        String courseName,
        EnrollmentStatus status,
        int progressRate,
        LocalDateTime expiredAt
) {
    public static EnrollmentListItemResult of(Enrollment enrollment, CourseSummary courseSummary, int progressRate) {
        return new EnrollmentListItemResult(
                enrollment.getId(),
                enrollment.getCourseId(),
                courseSummary.courseName(),
                enrollment.getStatus(),
                progressRate,
                enrollment.getExpiredAt()
        );
    }
}
