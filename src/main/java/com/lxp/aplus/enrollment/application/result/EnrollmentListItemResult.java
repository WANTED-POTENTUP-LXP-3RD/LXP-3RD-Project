package com.lxp.aplus.enrollment.application.result;

import java.util.List;


import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.application.port.out.CourseSummary;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentListItemResult(
        Long enrollmentId,
        Long courseId,
        String courseName,
        List<String> categories,
        EnrollmentStatus status,
        int overallProgressRate,
        LocalDateTime expiredAt
) {
    public static EnrollmentListItemResult of(Enrollment enrollment, CourseSummary courseSummary, int overallProgressRate, List<String> categories) {
        return new EnrollmentListItemResult(
                enrollment.getId(),
                enrollment.getCourseId(),
                courseSummary.courseName(),
                categories,
                enrollment.getStatus(),
                overallProgressRate,
                enrollment.getExpiredAt()
        );
    }
}
