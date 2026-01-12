package com.lxp.aplus.enrollment.infrastructure.persistence;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentRepository;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.enrollment.domain.StudentCountDto; // Updated import
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    private final EnrollmentJpaRepository jpaRepository;

    @Override
    public Enrollment save(Enrollment enrollment) {
        return jpaRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> saveAll(List<Enrollment> enrollments) {
        return jpaRepository.saveAll(enrollments);
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return jpaRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return jpaRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public Page<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status, Pageable pageable) {
        return jpaRepository.findByStudentIdAndStatus(studentId, status, pageable);
    }

    @Override
    public long countByCourseId(Long courseId) {
        return jpaRepository.countByCourseId(courseId);
    }

    @Override
    public boolean isEnrollmentCompleted(Long studentId, Long courseId) {
        return jpaRepository.findByStudentIdAndCourseId(studentId, courseId)
                .map(enrollment -> enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .orElse(false);
    }

    @Override
    public List<StudentCountDto> findStudentCountsByCourseIds(List<Long> courseIds) {
        return jpaRepository.findStudentCountsByCourseIds(courseIds);
    }
}
