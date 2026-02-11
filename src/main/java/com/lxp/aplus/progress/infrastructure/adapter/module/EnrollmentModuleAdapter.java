package com.lxp.aplus.progress.infrastructure.adapter.module;

import com.lxp.aplus.enrollment.application.internal.dto.EnrollmentInternalResult;
import com.lxp.aplus.enrollment.application.internal.usecase.EnrollmentInternalUseCase;
import com.lxp.aplus.progress.application.port.out.EnrollmentReader;
import com.lxp.aplus.progress.application.port.out.dto.EnrollmentStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnrollmentModuleAdapter implements EnrollmentReader {

    private final EnrollmentInternalUseCase enrollmentInternalUseCase;

    @Override
    public Optional<EnrollmentStatusDto> findEnrollment(Long userId, Long courseId) {
        return enrollmentInternalUseCase.findByStudentIdAndCourseId(userId, courseId)
                .map(this::toEnrollmentStatusDto);
    }

    private EnrollmentStatusDto toEnrollmentStatusDto(EnrollmentInternalResult enrollment) {
        boolean expired = enrollment.expiredAt() != null && enrollment.expiredAt().isBefore(LocalDateTime.now());
        return new EnrollmentStatusDto(enrollment.enrollmentId(), enrollment.isCompleted(), expired);
    }
}
