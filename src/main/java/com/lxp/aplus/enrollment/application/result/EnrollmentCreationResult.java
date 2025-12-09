package com.lxp.aplus.enrollment.application.result;

import java.util.List;

public record EnrollmentCreationResult(
        int totalCount,
        List<EnrollmentDetailResult> enrollments
) {
    public static EnrollmentCreationResult from(List<EnrollmentDetailResult> enrollments) {
        return new EnrollmentCreationResult(enrollments.size(), enrollments);
    }
}
