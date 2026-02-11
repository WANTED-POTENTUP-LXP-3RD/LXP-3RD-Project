package com.lxp.aplus.enrollment.application.port.out;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.enrollment.domain.StudentCountDto;
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

    boolean isEnrollmentCompleted(Long studentId, Long courseId);

    List<StudentCountDto> findStudentCountsByCourseIds(List<Long> courseIds);
}
