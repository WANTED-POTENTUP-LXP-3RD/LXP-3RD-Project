package com.lxp.aplus.review.infrastructure.adapter;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.review.application.port.out.EnrollmentFinder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentFinderAdapter implements EnrollmentFinder {
    private EnrollmentRepository enrollmentRepository;

    public EnrollmentFinderAdapter(EnrollmentRepository enrollmentRepository) {}

    @Override
    public Optional<Enrollment> findEnrollment(long userId, long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(userId, courseId);
    }

    @Override
    public boolean existsEnrollment(long userId, long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(userId, courseId);
    }
}
