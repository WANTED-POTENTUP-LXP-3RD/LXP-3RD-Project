package com.lxp.aplus.enrollment.infrastructure.persistence;

import com.lxp.aplus.enrollment.domain.Enrollment;
import com.lxp.aplus.enrollment.domain.EnrollmentStatus;
import com.lxp.aplus.enrollment.domain.StudentCountDto; // Updated import
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    Page<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status, Pageable pageable);
    long countByCourseId(Long courseId);

    @Query("""
            SELECT e.courseId as courseId, COUNT(e.id) as cnt
            FROM Enrollment e
            WHERE e.courseId in :courseIds
            GROUP BY e.courseId
            """)
    List<StudentCountDto> findStudentCountsByCourseIds(@Param("courseIds") List<Long> courseIds);
}
