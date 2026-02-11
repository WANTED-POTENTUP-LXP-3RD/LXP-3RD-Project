package com.lxp.aplus.enrollment.application.port.out;

import com.lxp.aplus.enrollment.application.port.out.dto.EnrollmentProgressDto;

public interface ProgressQueryPort {
    EnrollmentProgressDto getProgress(Long enrollmentId, Long courseId);

    boolean hasProgress(Long enrollmentId);

    void removeByEnrollmentId(Long enrollmentId);
}
