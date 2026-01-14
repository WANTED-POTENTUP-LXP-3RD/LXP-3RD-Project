package com.lxp.aplus.progress.application.port;

import java.util.Optional;

public interface EnrollmentReader {
    Optional<EnrollmentStatusDto> findEnrollment(Long userId, Long courseId);
}
