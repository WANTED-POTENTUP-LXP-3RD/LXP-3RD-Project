package com.lxp.aplus.enrollment.domain;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {

    Enrollment save(Enrollment enrollment);
    
    List<Enrollment> saveAll(List<Enrollment> enrollments);

    Optional<Enrollment> findById(Long id);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
