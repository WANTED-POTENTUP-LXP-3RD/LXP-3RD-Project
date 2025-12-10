package com.lxp.aplus.enrollment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    Enrollment save(Enrollment enrollment);
    
    List<Enrollment> saveAll(List<Enrollment> enrollments);

    Optional<Enrollment> findById(Long id);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    Page<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status, Pageable pageable);

    long countByCourseId(Long courseId);
}
