package com.lxp.aplus.enrollment.infrastructure.adapter;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.progress.application.port.EnrollmentReader;
import com.lxp.aplus.progress.application.port.EnrollmentStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class EnrollmentProviderAdapter implements EnrollmentReader {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public Optional<EnrollmentStatusDto> findEnrollment(Long userId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(userId, courseId)
                .map(this::toEnrollmentStatusDto);
    }

    private EnrollmentStatusDto toEnrollmentStatusDto(Enrollment enrollment) {
        return new EnrollmentStatusDto(
                enrollment.getId(),
                enrollment.isExpired(LocalDateTime.now())
        );
    }
}
