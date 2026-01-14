package com.lxp.aplus.review.application.port.out;

import com.lxp.aplus.enrollment.domain.Enrollment;
import java.util.Optional;

public interface EnrollmentQueryPort {
    Optional<Enrollment> findEnrollment(long userId, long courseId);
    boolean existsEnrollment(long userId, long courseId);
}
