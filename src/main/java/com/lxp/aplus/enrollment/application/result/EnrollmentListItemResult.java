package com.lxp.aplus.enrollment.application.result;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.application.port.out.CourseInfo;
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
    public static EnrollmentListItemResult of(Enrollment enrollment, CourseInfo courseInfo) {
        return new EnrollmentListItemResult(
                enrollment.getId(),
                enrollment.getCourseId(),
                courseInfo.courseName(),
                enrollment.getStatus(),
                enrollment.getProgressRate(),
                enrollment.getExpiredAt()
        );
    }
}
