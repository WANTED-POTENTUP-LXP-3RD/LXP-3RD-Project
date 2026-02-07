package com.lxp.aplus.progress.application.port.out;

import com.lxp.aplus.progress.application.port.out.dto.EnrollmentStatusDto;

import java.util.Optional;

public interface EnrollmentReader {
    Optional<EnrollmentStatusDto> findEnrollment(Long userId, Long courseId);
}
