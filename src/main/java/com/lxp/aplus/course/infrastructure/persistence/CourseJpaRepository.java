package com.lxp.aplus.course.infrastructure.persistence;

import com.lxp.aplus.course.domain.Course;
import com.lxp.aplus.course.domain.CourseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndCourseStatusNot(Long id, CourseStatus courseStatus);

    Page<Course> findAllByInstructorIdAndCourseStatusNot(Long instructorId, CourseStatus courseStatus,
                                                         Pageable pageable);

    Page<Course> findAllByCourseStatus(CourseStatus courseStatus, Pageable pageable);

    List<Course> findByIdIn(List<Long> ids);
}
