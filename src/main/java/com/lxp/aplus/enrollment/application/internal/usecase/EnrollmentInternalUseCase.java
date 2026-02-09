package com.lxp.aplus.enrollment.application.internal.usecase;

import com.lxp.aplus.enrollment.application.internal.dto.EnrollmentInternalResult;
import com.lxp.aplus.common.error.BusinessException;
import com.lxp.aplus.common.error.code.EnrollmentErrorCode;
import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentInternalUseCase {
    private final EnrollmentRepository enrollmentRepository;

    public Optional<EnrollmentInternalResult> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .map(EnrollmentInternalResult::from);
    }

    @Transactional
    public void completeEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND_OR_NO_ACCESS));
        enrollment.completeEnrollment();
        enrollmentRepository.save(enrollment);
    }
}
