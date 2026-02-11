package com.lxp.aplus.enrollment.application.port.in;

import com.lxp.aplus.enrollment.application.command.EnrollmentCommand;
import com.lxp.aplus.enrollment.domain.Enrollment;

public interface EnrollmentCommandUseCase {
    Long enroll(EnrollmentCommand command);

    Enrollment cancel(Long enrollmentId, Long studentId);
}
