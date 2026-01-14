package com.lxp.aplus.enrollment.presentation.response;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import lombok.Getter;

@Getter
public class EnrollmentCancelResponse {

    private final Long enrollmentId;
    private final EnrollmentStatus status;

    private EnrollmentCancelResponse(Long enrollmentId, EnrollmentStatus status) {
        this.enrollmentId = enrollmentId;
        this.status = status;
    }

    public static EnrollmentCancelResponse from(Enrollment enrollment) {
        return new EnrollmentCancelResponse(enrollment.getId(), enrollment.getStatus());
    }
}
